extern crate warp;

use std::convert::Infallible;

use flume::Sender;
use prometheus::{Encoder, Registry, TextEncoder};
use serde::{Deserialize, Serialize};
use warp::Filter;

pub trait PrometheusRegistry {
    fn register_metric(&self, r: &Registry);
}

#[derive(Deserialize, Serialize, Debug)]
struct HarborEventTag {
    digest: String,
    tag: String,
    resource_url: String,
}

#[derive(Deserialize, Serialize, Debug)]
struct HarborEventRepository {
    date_created: u64,
    name: String,
    namespace: String,
    repo_full_name: String,
    repo_type: String,
}

#[derive(Deserialize, Serialize, Debug)]
struct HarborEventData {
    tags: Vec<HarborEventTag>,
    repository: HarborEventRepository,
}

#[derive(Deserialize, Serialize, Debug)]
struct HarborEvent {
    event_data: HarborEventData,
}

#[cfg(target_os = "linux")]
fn collect_process_metrics(buffer: &mut Vec<u8>, encoder: &TextEncoder) {
    // 收集进程参数
    let process_metric = prometheus::gather();
    let mut process_m_buffer = Vec::new();
    encoder
        .encode(&process_metric, &mut process_m_buffer)
        .unwrap();

    buffer.append(&mut process_m_buffer);
}

#[cfg(not(target_os = "linux"))]
fn collect_process_metrics(_buffer: &mut Vec<u8>, _encoder: &TextEncoder) {}

async fn got_harbor_event(
    harbor_event: HarborEvent,
    update_image_queue_sender: Sender<String>,
) -> Result<impl warp::Reply, Infallible> {
    debug!("收到 Harbor webhook 事件: {:?}", harbor_event);
    for tag in harbor_event.event_data.tags.iter() {
        update_image_queue_sender
            .send(tag.resource_url.clone())
            .unwrap();
    }
    Ok(warp::reply())
}

fn post_json() -> impl Filter<Extract = (HarborEvent,), Error = warp::Rejection> + Clone {
    // body 转为 json 格式
    // 拒掉过大的 body
    warp::body::content_length_limit(1024 * 16).and(warp::body::json::<HarborEvent>())
}

pub async fn run(r: Registry, update_image_queue_sender: Sender<String>) {
    // GET /metrics => 200 OK with body prometheus metric
    let metric_path = warp::path!("metrics").map(move || {
        let encoder = TextEncoder::new();
        let mut buffer = vec![];

        // 收集性能参数
        let self_metric = r.gather();
        encoder.encode(&self_metric, &mut buffer).unwrap();

        // 收集进程参数
        collect_process_metrics(&mut buffer, &encoder);

        String::from_utf8(buffer).unwrap()
    });

    let healthz_path = warp::path!("healthz").map(|| "Running");

    let webhook = warp::path("webhook")
        .and(warp::post())
        .and(post_json())
        .and(warp::any().map(move || update_image_queue_sender.clone()))
        .and_then(got_harbor_event);

    let routes_get = warp::get().and(metric_path.or(healthz_path));

    let routes = routes_get.or(webhook);
    warp::serve(routes).run(([0, 0, 0, 0], 6061)).await;
}
