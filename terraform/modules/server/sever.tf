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