function toggleSideBar(sidebarToggled) {
  let sidebar = document.getElementsByClassName("sidebar-mobile")[0];
  let sidebarSection = document.getElementsByClassName("sidebar-section")[0];
  if (sidebarToggled) {
    sidebar.classList.remove("show");
    sidebarSection.classList.remove("show");
  } else {
    sidebar.classList.add("show");
    sidebarSection.classList.add("show");
  }
}
