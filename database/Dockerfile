FROM postgres:16-bookworm

# Install PostGIS 3 extension packages natively for host architecture (ARM64 / AMD64)
RUN apt-get update \
    && apt-get install --no-install-recommends -y \
       postgresql-16-postgis-3 \
       postgresql-16-postgis-3-scripts \
    && rm -rf /var/lib/apt/lists/*
