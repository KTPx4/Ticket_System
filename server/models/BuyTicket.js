const mongoose = require('mongoose')
const moment = require("moment");
const {Schema} = mongoose

const BuyTicketSchema = new mongoose.Schema({
    members: [
        {
            type: Schema.Types.ObjectId,
            ref: 'accounts'
        }
    ],
    accCreate:{
      type: Schema.Types.ObjectId,
      ref: 'accounts'
    },
    event:{
      type: Schema.Types.ObjectId,
      ref: 'events'
    },
    ticketInfo: [
        {
            type: Schema.Types.ObjectId,
            ref: 'ticket_infos'
        }
    ],
    date: {
        type: Date,
        default: null,
        get: (v) => moment(v).format('DD/MM/YYYY HH:mm') // Getter định dạng
    },
    discount: {
        type: Schema.Types.ObjectId,
        ref: "discounts",
        default: null
    },
    coupon:{
        type: Schema.Types.ObjectId,
        ref: "coupons",
        default: null
    },
    typePayment:{
        type: String,
        default: "single" // single or all
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
    status:{
        type:String,
        default: "waiting"
    }

}, {
    toJSON: { getters: true }, // Bật chế độ dùng getter khi convert sang JSON
    toObject: { getters: true } // Bật chế độ dùng getter khi convert sang Object
})

module.exports = mongoose.model('buy_tickets', BuyTicketSchema)