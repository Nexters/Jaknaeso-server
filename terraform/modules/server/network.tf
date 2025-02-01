resource "ncloud_access_control_group" "jaknaeso_server_acg_01" {
  vpc_no      = var.vpc_no
  name        = "${var.name_terra}-acg-01"
  description = "${var.name_terra} Access Control Group 01"
}

resource "ncloud_access_control_group_rule" "jaknaeso_server_acg_01_rule_01" {
  access_control_group_no = ncloud_access_control_group.jaknaeso_server_acg_01.id

  dynamic "inbound" {
    for_each = var.admin_ip_cidrs
    content {
      protocol    = "TCP"
      ip_block    = inbound.value
      port_range  = "22"
      description = "SSH access from ${inbound.value}"
    }
    content {
      protocol    = "TCP"
      ip_block    = inbound.value
      port_range  = "3306"
      description = "MySQL access from ${inbound.value}"
    }
  }

  dynamic "inbound" {
    for_each = var.admin_ip_cidrs
    content {
      protocol = "TCP"
        ip_block = inbound.value
        port_range = "3306"
        description = "MySQL access from ${inbound.value}"
    }
  }

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "80"
    description = "Backend server HTTP port"
  }

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "443"
    description = "Backend server HTTPS port"
  }

  outbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "All TCP outbound"
  }

}

resource "ncloud_network_interface" "jaknaeso_server_nic_01" {
  access_control_groups = [ncloud_access_control_group.jaknaeso_server_acg_01.id]
  subnet_no = var.subnet_public_id
  name      = "${var.name_terra}-jaknaeso-nic-01"
}
resource "ncloud_network_interface" "jaknaeso_swagger_nic_01" {
  access_control_groups = [ncloud_access_control_group.jaknaeso_server_acg_01.id]
  subnet_no = var.subnet_public_id
  name      = "${var.name_terra}-swagger-nic-01"
}

resource "ncloud_public_ip" "jaknaeso_public_ip" {
  server_instance_no = ncloud_server.jaknaeso_server.instance_no
  description        = "for ${ncloud_server.jaknaeso_server.name} public ip"
}

resource "ncloud_public_ip" "jaknaeso_swagger_public_ip" {
  server_instance_no = ncloud_server.jaknaeso_swagger_server.instance_no
  description        = "for ${ncloud_server.jaknaeso_swagger_server.name} public ip"
}