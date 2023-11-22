const controller = {
  getHome: function (req, res) {
    res.render("start", {
      //   pageName: "File Exchange",
      mainCSS: "../public/css/main.css",
      mainJS: "../public/js/main.js",
    });
  },
};

export default controller;
