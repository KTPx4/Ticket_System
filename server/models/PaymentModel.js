const mongoose = require('mongoose')
const moment = require("moment");
const {Schema} = mongoose

const PaymentSchema = new mongoose.Schema({
    type: {
        type: String
    }
})