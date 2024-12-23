const CouponModel = require('../../models/CouponModel')

module.exports.Create = async(req, res, next)=>{
    var {maxDiscount, percentDiscount, count} = req.body

    // Kiểm tra nếu có maxDiscount và nếu nó là số và > 0
    if (maxDiscount !== undefined && (typeof maxDiscount !== 'number' || maxDiscount <= 0)) {
        return res.status(400).json({
            message: "maxDiscount phải là số và lớn hơn 0"
        });
    }

    // Kiểm tra nếu có percentDiscount và nếu nó là số và trong khoảng 0 < percentDiscount < 99
    if (percentDiscount !== undefined && (typeof percentDiscount !== 'number' || percentDiscount <= 0 || percentDiscount >= 99)) {
        return res.status(400).json({
            message: "percentDiscount phải là số và nằm trong khoảng từ 0 đến 99"
        });
    }

    // Kiểm tra nếu có count và nếu nó là số và > 0
    if (count !== undefined && (typeof count !== 'number' || count <= 0)) {
        return res.status(400).json({
            message: "count phải là số và lớn hơn 0"
        });
    }

    // Nếu tất cả các điều kiện hợp lệ thì tiếp tục xử lý
    return next();

}

module.exports.ChangeCoupon = async (req, res, next)=>{
    try{
        var {type} = req.query
        if(!type)
        {
            return res.status(400).json({
                message: "Vui lòng chọn loại vé muốn đổi"
            })
        }
        if(!(typeof type !== "number"))
        {
            return res.status(400).json({
                message: "Loại vé hiện tại là kiểu số nguyên"
            })
        }
        return next()

    }
    catch (e) {
        console.log("Coupon Validator - ChangeCoupon", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau!"
        })
    }
}

module.exports.DiscountAll = async (req, res, next)=>{
    return next()
}