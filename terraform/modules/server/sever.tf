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

resource "ncloud_server" "jaknaeso_swagger_server" {
  subnet_no           = var.subnet_public_id
  name                = "jaknaeso-swagger-server"
  login_key_name      = ncloud_login_key.jaknaeso_server_login_key.key_name
  server_spec_code    = data.ncloud_server_specs.kvm-spec.server_spec_list.0.server_spec_code
  server_image_number = data.ncloud_server_image_numbers.kvm_image.image_number_list.0.server_image_number

  network_interface {
    network_interface_no = ncloud_network_interface.jaknaeso_swagger_nic_01.id
    order                = 0
  }
}