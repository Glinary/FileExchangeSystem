import { Router } from "express";
import controller from "../controllers/controller.js";

const router = Router();

router.get("/", controller.getHome);

router.post("/join", controller.postJoin);

export default router;
