# ------------------------------------------------------------------------------
# Cargo Build Stage
# ------------------------------------------------------------------------------

FROM rust:latest as cargo-build
RUN rustup component add rustfmt
WORKDIR /usr/src/scheduler_controller

RUN mkdir "$HOME/.cargo"
COPY ./cargo_crates.config "$HOME/.cargo/config"

# reduce compile time(benefit from docker's cache)
RUN USER=root cargo init
COPY Cargo.lock .
COPY Cargo.toml .
RUN cargo build --release

COPY . .
RUN cargo build --release


# ------------------------------------------------------------------------------
# Final Stage
# ------------------------------------------------------------------------------

FROM debian:stretch-slim
WORKDIR /usr/local/bin/
COPY --from=cargo-build /usr/src/scheduler_controller/target/release/scheduler_controller /usr/local/bin/scheduler_controller
CMD ["scheduler_controller"]