const CouponModel = require('../models/CouponModel')

module.exports.Create = async (req, res)=>{

    try {
        var {maxDiscount, percentDiscount, count, name , desc, type, code} = req.body
        // Tạo đối tượng mới từ schema của Coupon
        const newCoupon = new CouponModel({
            name: name || "Mã giảm giá", // Nếu không có thì sử dụng giá trị mặc định
            desc: desc || "Đây là mã giảm giá cho bạn, số lượng có hạn. Hãy nhanh tay!",
            type: type || "public", // Nếu không có thì không gán
            maxDiscount: maxDiscount !== undefined ? maxDiscount : -1, // Kiểm tra giá trị tồn tại
            percentDiscount: percentDiscount !== undefined ? percentDiscount : 5, // Kiểm tra giá trị tồn tại
            count: count !== undefined ? count : 10, // Kiểm tra giá trị tồn tại
            code
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

module.exports.GetAllPublic = async (req, res)=>{
    try{
        var {User} = req.vars
        var coupons = await CouponModel.find({
            type: "public",
            isValid:true,
            count: { $gt: 0 } // Điều kiện count > 0
        })

        return res.status(200).json({
            point: User?.point??0,
            length: coupons?.length ?? 0,
            message: "Lấy thành công",
            data: coupons
        })
    }
    catch (e) {
        console.log("Coupon Controller - GetALl", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau!"
        })
    }
}

module.exports.GetMyCoupon = async(req, res)=>{
    try{
        var {User}= req.vars
        var coupons = await CouponModel.find({
            type: "private",
            acc: User._id,
            isValid: true
        })

        return res.status(200).json({
            point: User?.point??0,
            length: coupons?.length ?? 0,
            message: "Lấy thành công",
            data: coupons
        })
    }
    catch (e) {
        console.log("Coupon Controller - GetMyCoupon", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau!"
        })
    }
}

module.exports.ChangeCoupon = async (req,res)=>{
    try{
        var {User} = req.vars
        var userId = User._id
        var point  = User.point ?? 0
        var {type} = req.query
        type = parseInt(req.query.type, 10); // Chuyển chuỗi thành số

        if(point < type)
        {
            return res.status(400).json({
                message: "Điểm của bạn không đủ để đổi loại vé này"
            })
        }
        else{
            var percentDiscount = 0
            switch (type) {
                case 2000:
                    percentDiscount = 7
                    break;

                case 3000:
                    percentDiscount = 9
                    break;

                case 4000:
                    percentDiscount = 10
                    break;

                case 5000:
                    percentDiscount = 11
                    break;

                case 10000:
                    percentDiscount = 25
                    break;

                default:
                    break;
            }

            if(percentDiscount === 0)
            {
                return res.status(400).json({
                    message: "Không có loại vé phù hợp để đổi"
                })
            }

            var pointAfter = point - type

            Coupon = await CouponModel.create({
                name:  `Mã giảm giá ${percentDiscount}%`, // Nếu không có thì sử dụng giá trị mặc định
                desc:  "Đây là mã giảm giá dành riêng cho bạn. Đừng tiết lộ code cho bất cứ ai",
                type:  "private", // Nếu không có thì không gán
                maxDiscount: -1, // Kiểm tra giá trị tồn tại
                percentDiscount: percentDiscount, // Kiểm tra giá trị tồn tại
                count: 1, // Kiểm tra giá trị tồn tại
                acc: userId
            })

            User.point = pointAfter

            await User.save()
            return res.status(200).json({
                point: User?.point??0,
                length: 1,
                message: "Đổi mã giảm giá thành công",
                data: [Coupon]
            })

        }


    }
    catch (e) {
        console.log("Coupon Controller - ChangeCoupon", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau!"
        })
    }
}