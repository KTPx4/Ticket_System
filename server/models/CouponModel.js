const mongoose = require('mongoose')
const {Schema}= mongoose
const Generate = require('../modules/Generate')
const CouponSchema = new mongoose.Schema({
    code:{
      type: String,
      unique: true
    },
    name: {
        type:String,
        default: "Mã giảm giá"
    },
    desc: {
        type: String,
        default: "Đây là mã giảm giá cho bạn, số lượng có hạn. Hãy nhanh tay!"
    },
    type: {
        type: String,
        default: "public"
    },
    maxDiscount: {
        type: Number,
        default: -1
    },
    percentDiscount:{
        type: Number,
        default: 5
    },
    count:{
        type: Number,
        default: 10
    },
    isValid:{
        type: Boolean,
        default: true
    },
    acc:{
        type: Schema.Types.ObjectId,
        ref: 'accounts'
    }
})

CouponSchema.pre('save', async function (next) {
    if (!this.code) {
        let isUnique = false;
        let newCode;

        // Lặp để tạo mã không trùng
        while (!isUnique) {
            newCode = Generate.RandomChar(5);
            const existingDoc = await mongoose.model('coupons').findOne({ code: newCode });
            if (!existingDoc) {
                isUnique = true;
            }
        }
        this.code = newCode; // Gán giá trị mã mới vào tài liệu
    }
    next();
});

module.exports = mongoose.model('coupons', CouponSchema)