use std::io::Write;

pub fn encode_workflows_use_snappy(workflows: &mut Vec<Vec<u8>>) -> std::io::Result<Vec<Vec<u8>>> {
    debug!("压缩 {} 个工作流", workflows.len());
    let mut compressed_workflows: Vec<Vec<u8>> = Vec::new();
    for wf in workflows.iter_mut() {
        let mut wtr = snap::write::FrameEncoder::new(Vec::new());
        wtr.write_all(&mut wf[..])?;
        compressed_workflows.push(wtr.into_inner().unwrap());
    }
    Ok(compressed_workflows)
}
