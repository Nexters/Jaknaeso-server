resource "ncloud_login_key" "jaknaeso_server_login_key" {
  key_name = "${var.name_terra}-jaknaeso-key"
}

resource "local_file" "ncp_pem" {
  filename = "${ncloud_login_key.jaknaeso_server_login_key.key_name}.pem"
  content  = ncloud_login_key.jaknaeso_server_login_key.private_key
}

resource "ncloud_access_control_group" "jaknaeso_server_acg_01" {
  vpc_no      = var.vpc_no
  name        = "${var.name_terra}-acg-01"
  description = "${var.name_terra} Access Control Group 01"
}

resource "ncloud_access_control_group_rule" "jaknaeso_server_acg_01_rule_01" {
  access_control_group_no = ncloud_access_control_group.jaknaeso_server_acg_01.id

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "22"
    description = "SSH access"
  }

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "8080"
    description = "Backend server port"
  }

  outbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "All TCP outbound"
  }

  outbound {
    protocol    = "UDP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "All UDP outbound"
  }
}

resource "ncloud_network_interface" "jaknaeso_server_nic_01" {
  access_control_groups = [ncloud_access_control_group.jaknaeso_server_acg_01.id]
  subnet_no = var.subnet_public_id
  name      = "${var.name_terra}-jaknaeso-nic-01"
}

data "ncloud_server_image" "server_image" {
  filter {
    name = "product_name"
    values = ["ubuntu-20.04"]
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

resource "ncloud_server" "jaknaeso_server" {
  subnet_no                 = var.subnet_public_id
  name                      = "jaknaseo-server"
  server_image_product_code = data.ncloud_server_image.server_image.id
  server_product_code       = data.ncloud_server_product.product.id
  login_key_name            = ncloud_login_key.jaknaeso_server_login_key.key_name

  network_interface {
    network_interface_no = ncloud_network_interface.jaknaeso_server_nic_01.id
    order                = 0
  }
}

data "ncloud_root_password" "default" {
  server_instance_no = ncloud_server.jaknaeso_server.instance_no
  private_key        = ncloud_login_key.jaknaeso_server_login_key.private_key
}

resource "local_file" "jaknaeso_server_root_password" {
  filename = "jaknaeso-server-root_password.txt"
  content  = "jaknaeso-server => ${data.ncloud_root_password.default.root_password}"
}

resource "ncloud_public_ip" "jaknaeso_public_ip" {
  server_instance_no = ncloud_server.jaknaeso_server.instance_no
  description        = "for ${ncloud_server.jaknaeso_server.name} public ip"
}