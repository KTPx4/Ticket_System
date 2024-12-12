const mongoose = require('mongoose')
const moment = require("moment");
const {Schema} = mongoose

const OrderSchema = new mongoose.Schema({
    accBuy:{
        type: Schema.Types.ObjectId,
        ref:'accounts'
    },
    buyTicket:{
        type: Schema.Types.ObjectId,
        ref: 'buy_tickets'
    },
    ticketInfo: [
        {
            type: String
        }
    ], 
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
        type: String,
        default: "momo"  
    },
    price:{
        type: Number,
        default: 0
    },
    discountMoney:{
        type: Number,
        default: 0
    },
    CouponDiscountMoney:{
        type: Number,
        default: 0
    },
    priceFinal:{
        type: Number,
        default: 0
    },
    date: {
        type: Date,
        default: null,
        get: (v) => moment(v).format('DD/MM/YYYY HH:mm') // Getter định dạng
    },    
    status:{
        type:String,
        default: "waiting"
    },    
    createdAt: { 
        type: Date, 
        default: Date.now 
    } // Thời gian tạo đơn hàng
}, 
    {
        toJSON: { getters: true }, // Bật chế độ dùng getter khi convert sang JSON
        toObject: { getters: true } // Bật chế độ dùng getter khi convert sang Object
    }
)

module.exports = mongoose.model('orders', OrderSchema)