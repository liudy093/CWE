fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::configure()
        .build_client(false)
        .build_server(true)
        .out_dir("src/grpc/")
        .format(true)
        .compile(&["grpc_proto/scheduler_controller.proto"], &["grpc_proto"])?;

    tonic_build::configure()
        .build_client(true)
        .build_server(false)
        .out_dir("src/grpc/")
        .format(true)
        .compile(
            &[
                "grpc_proto/resource_allocator.proto",
                "grpc_proto/scheduler.proto",
                "grpc_proto/PodsPowerPredict.proto",
            ],
            &["grpc_proto"],
        )?;

    Ok(())
}
