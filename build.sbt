name := """reactive-stocks-root"""
version := "latest"

lazy val reactiveStocksRoot = (project in file(".")).aggregate(play_module, seed_app)

lazy val play_module = project
lazy val seed_app = project
