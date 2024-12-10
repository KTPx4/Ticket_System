const CouponModel = require('../models/CouponModel')

module.exports.Create = async (req, res)=>{


    try {
        var {maxDiscount, percentDiscount, count, name , desc, type} = req.body
        // Tạo đối tượng mới từ schema của Coupon
        const newCoupon = new CouponModel({
            name: name || "Mã giảm giá", // Nếu không có thì sử dụng giá trị mặc định
            desc: desc || "Đây là mã giảm giá cho bạn, số lượng có hạn. Hãy nhanh tay!",
            type: type || "public", // Nếu không có thì không gán
            maxDiscount: maxDiscount !== undefined ? maxDiscount : -1, // Kiểm tra giá trị tồn tại
            percentDiscount: percentDiscount !== undefined ? percentDiscount : 5, // Kiểm tra giá trị tồn tại
            count: count !== undefined ? count : 10 // Kiểm tra giá trị tồn tại
        });
        // Lưu vào database
        const savedCoupon = await newCoupon.save();
        return res.status(201).json({
            message: "Tạo mã giảm giá thành công",
            data:savedCoupon
        }); // Trả về đối tượng coupon đã lưu
    }
    catch (error) {
        console.log("Create Coupon - controller: ", error)
        return res.status(500).json({ error: "Đã có lỗi xảy ra khi tạo mã giảm giá!" });
    }
}