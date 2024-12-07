const mongoose = require('mongoose')
const {Schema} = mongoose

const DiscountSchema = new mongoose.Schema({
    event: {
        type: Schema.Types.ObjectId,
        ref: 'events',
    },
    memberRange: Number,
    percentDiscount: {
        type: Number,
        default: 0
    }

})

module.exports = mongoose.model('discounts', DiscountSchema)