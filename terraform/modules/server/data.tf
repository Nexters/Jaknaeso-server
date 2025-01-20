data "ncloud_server_image" "server_image" {
  filter {
    name = "product_name"
    values = ["ubuntu-20.04"]
  }
}

data "ncloud_server_image_numbers" "kvm_image" {
  server_image_name = "ubuntu-22.04-base"
  filter {
    name = "hypervisor_type"
    values = ["KVM"]
  }
}

data "ncloud_server_specs" "kvm-spec" {
  filter {
    name = "server_spec_code"
    values = ["mi1-g3"]
  }
  filter {
    name = "block_storage_max_count"
    values = ["10GB"]
  }
}

data "ncloud_server_product" "product" {
  server_image_product_code = data.ncloud_server_image.server_image.id

  filter {
    name  = "product_code"
    values = ["SSD"]
    regex = true
  }

  filter {
    name = "cpu_count"
    values = ["2"]
  }
  filter {
    name = "memory_size"
    values = ["4GB"]
  }
  filter {
    name = "product_type"
    values = ["HICPU"]
  }
}

data "ncloud_root_password" "default" {
  server_instance_no = ncloud_server.jaknaeso_server.instance_no
  private_key        = ncloud_login_key.jaknaeso_server_login_key.private_key
}