import express from "express";
import bodyParser from "body-parser";

const app = express();
app.use(bodyParser.json())

const controller = {
  getHome: async function (req, res) {
    res.render("start", {
      //   pageName: "File Exchange",
      mainCSS: "static/css/main.css",
      mainJS: "static/js/main.js",
      script1: "static/js/commands.js",
    });
  },

  postJoin: async function (req, res) {
    let {userInput} = req.body;

    if (userInput) {
      try {
        console.log(userInput);

        // connect to java at this point
      } catch (e) {
        console.log("error",);
      }
    }

    
    res.json(userInput);
  }
};

export default controller;
