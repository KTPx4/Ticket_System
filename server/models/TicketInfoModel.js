const mongoose = require('mongoose')
const moment = require("moment");
const {Schema} = mongoose

const TicketInfoSchema = new mongoose.Schema({
    ticket:{
        type: Schema.Types.ObjectId,
        ref: "tickets"
    },
    event:{
        type: Schema.Types.ObjectId,
        ref: 'events'
    },
    coupon:{
        type: Schema.Types.ObjectId,
        ref: "coupons",
        default: null
    },
    payment:{
        type: Schema.Types.ObjectId,
        ref: "payments"
    },
    price:{
        type: Number,
        default: 0
    },
    priceFinal:{
        type: Number,
        default: 0
    },
    moneyGive:{
        type: Number,
        default: 0
    },
    moneyChange:{
        type: Number,
        default: 0
    },
    accPay:{
        type: Schema.Types.ObjectId,
        ref: "accounts"
    },
    date: {
        type: Date,
        default: null,
        get: (v) => moment(v).format('DD/MM/YYYY HH:mm') // Getter định dạng
    },
    status:{
        type:String,
        default: "waiting"
    }
})

module.exports = mongoose.model('ticket_infos', TicketInfoSchema)