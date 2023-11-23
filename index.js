import express from "express";
import bodyParser from "body-parser";
import exphbs from "express-handlebars";
import routes from "./routes/routes.js";
import "dotenv/config";

const port = process.env.PORT;

const app = express();
// Set public folder to 'static' for accessibility (refer to controller.js)
app.use("/static", express.static("public"));

app.engine("hbs", exphbs.engine({ extname: "hbs" }));
app.set("view engine", "hbs"); // set express' default templating engine
app.set("views", "./views");

//user bodyParser
app.use(bodyParser.json())

// use router
app.use(routes);

// activate the app instance
app.listen(port, () => {
  console.log(`Server is running at:`);
  console.log(`http://localhost:` + port);
});
