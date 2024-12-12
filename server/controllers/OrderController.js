const TicketInfoModel = require('../models/TicketInfoModel')
const TicketModel = require('../models/TicketModel')
const AccountModel = require('../models/AccountModel')
const BuyTicketModel = require('../models/BuyTicket')
const OrderModel = require('../models/OrderModel')
const EventModel = require("../models/EventModel");
const jwt = require("jsonwebtoken");
const SECRET_ORDER = process.env.SECRET_ORDER || "secret-key-order-px4"
const SECRET_ORDER_START = process.env.SECRET_ORDER_START || "secret-key-"
const SECRET_ORDER_END = process.env.SECRET_ORDER_END || "order-px4"

const SECRET_STRIPE = process.env.SECRET_STRIPE
const MIN_MONEY_PAY = 20000;
const stripe = require('stripe')(SECRET_STRIPE); // Thay bằng Stripe Secret Key

module.exports.GetAll= async(req, res)=>{
    try{
        var {User} = req.vars
        var listBuyTicket = await BuyTicketModel.find({
            $or: [
                { accCreate: User._id },
                { members: User._id }
            ]
        })
            .populate('members')
            .populate('accCreate')
            .populate('event')
            .populate('ticketInfo')
            .populate('discount')
            .populate('coupon')
            .populate('payment')

        return res.status(200).json({
            message: "Lấy danh sách thành công",
            data: listBuyTicket
        })
    }
    catch (e)
    {
        console.log("OrderController - GetAll:", e)
        return res.status(500).json({
            message: "Lỗi server, thử lại sau"
        })
    }
}
module.exports.GetByEvent = async(req, res)=>{
    try{
        var {Event, User} = req.vars
        var listBuyTicket = await BuyTicketModel.find({
            event: Event._id,
            $or: [
                { accCreate: User._id },
                { members: User._id }
            ]
        })
            .populate('members')
            .populate('accCreate')
            .populate('event')
            .populate('ticketInfo')
            .populate('discount')
            .populate('coupon')
            .populate('payment')

        return res.status(200).json({
            message: "Lấy thành công",
            data: listBuyTicket
        })
    }
    catch (e)
    {
        console.log("OrderController - GetAll:", e)
        return res.status(500).json({
            message: "Lỗi server, thử lại sau"
        })
    }
}

module.exports.Create = async (req, res) => {
    try {

        const { Event, User, ListTicket } = req.vars;

        const userId = User._id;
        const eventId = Event._id;

        // Tìm và tạo mới các ticket info nếu chưa tồn tại
        const listInfo = await Promise.all(
            ListTicket.map(async (ticket) => {
                let TicketInfo = await TicketInfoModel.findOne({
                    event: eventId,
                    ticket: ticket
                });

                if (!TicketInfo) {
                    TicketInfo = await TicketInfoModel.create({
                        event: eventId,
                        ticket: ticket,
                    });
                }
                return TicketInfo._id; // Chỉ trả về ID để giảm kích thước dữ liệu
            })
        );

        // Kiểm tra nếu đã có BuyTicket
        let existBuyTicket = await BuyTicketModel.findOne({
            accCreate: userId,
            event: eventId,
            status: "waiting",
            isDeleted: false
        });

        if (existBuyTicket) {
            // Lọc những ticketInfo chưa có
            const newTickets = listInfo.filter((id) => !existBuyTicket.ticketInfo.includes(id));

            // Thêm các ticket info mới vào danh sách
            existBuyTicket.ticketInfo.push(...newTickets);

            await existBuyTicket.save();

            return res.status(200).json({
                message: "Đã thêm vé",
                data: existBuyTicket,
            });

        } else {
            // Tạo mới BuyTicket
            const BuyTicket = await BuyTicketModel.create({
                accCreate: userId,
                ticketInfo: listInfo,
                event: eventId,
            });

            return res.status(201).json({
                message: "Đã tạo đơn hàng thành công",
                data: BuyTicket,
            });
        }
    } catch (error) {
        console.error("OrderController - Create: ", error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
    }
}

module.exports.Update = async(req, res)=> {
    try {
        const {BuyTicket, User, Update} = req.vars;



        const updatedEvent = await BuyTicketModel.findOneAndUpdate(
            {
                _id: BuyTicket._id
            },
            {$set: Update}, // Cập nhật chỉ các trường hợp lệ
            {new: true, runValidators: true} // Trả về dữ liệu sau khi cập nhật và kiểm tra tính hợp lệ
        )
            .populate({
                path: 'members', // Liên kết members
                select: 'name email image address point' // Chỉ trả về các trường cần thiết
            })
            .populate('event') // Liên kết event
            .populate({
                path: 'accCreate', // Liên kết accCreate
                select: 'name email image address point' // Chỉ trả về các trường cần thiết
            })
            .populate({
                path: 'ticketInfo', // Liên kết ticketInfo
                match: {
                    $or: [
                        { accPay: { $exists: false } }, // ticketInfo không có accPay
                        { 'ticket.accBuy': { $exists: false } } // ticket trong ticketInfo không có accBuy
                    ]
                },
                populate: {
                    path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                    populate: {
                        path: 'info', // Liên kết tiếp tới info trong ticket
                        model: 'ticket_types'
                    }
                }
            })

        return res.status(200).json({
            message: "Chỉnh sửa thành công",
            data: updatedEvent,
        });

    } catch (error) {
        console.error("OrderController - Update: ", error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
    }

}

module.exports.GetCheckOut = async(req, res)=>{
    try{
        var {User, Coupon, BuyTicket, Infos} = req.vars
        var {infos} = req.body
        var CouponDiscount = 0
        var totalPrice = 0
        var DiscountMoney = 0
        var FinalMoney = 0

        if(BuyTicket.typePayment === "all")
        {
            // Tính tổng `price` từ `ticket_types`
            totalPrice = BuyTicket.ticketInfo.reduce((total, ticketInfo) => {
                const ticketPrice = ticketInfo.ticket?.info?.price || 0; // Lấy giá tiền, nếu không có thì là 0
                return total + ticketPrice;
            }, 0);
            DiscountMoney = BuyTicket.discount?.percentDiscount ? (BuyTicket.discount?.percentDiscount*totalPrice)/100 : 0
        }
        else if(BuyTicket.typePayment === "single")
        {
             totalPrice = Infos.reduce((acc, info) => {
                acc += info.ticket.info.price;
                return acc;
            }, 0);
        }
        else{
            return res.status(400)
                .json({
                    message: "Loại thanh toán không hợp lệ"
                })
        }

        if(Coupon)
        {
            CouponDiscount = Coupon.maxDiscount < 0
                ? (Coupon.percentDiscount * totalPrice) / 100
                : Math.min((Coupon.percentDiscount * totalPrice) / 100, Coupon.maxDiscount);
        }

        FinalMoney = Math.max(totalPrice - DiscountMoney - CouponDiscount, MIN_MONEY_PAY)

        var listIDInfos = []
        if(BuyTicket.typePayment === "single" && Infos && Array.isArray(Infos) && Infos.length > 0)
        {
            listIDInfos = Infos.map((info)=> info._id.toString())
        }

        var order = await OrderModel.create({
            accBuy: User._id,
            buyTicket: BuyTicket._id,
            ticketInfo: listIDInfos,
            discount: BuyTicket.discount?._id,
            coupon: Coupon?._id,
            typePayment: BuyTicket.typePayment,
            price: totalPrice,
            discountMoney: DiscountMoney,
            CouponDiscountMoney: CouponDiscount,
            priceFinal: FinalMoney
        })

        var data = {
            User: User,
            Type:  BuyTicket.typePayment.toLowerCase(),
            BuyTicket: BuyTicket,
            Coupon: Coupon??"",
            Price: totalPrice,
            Discount: DiscountMoney,
            CouponDiscount: CouponDiscount,
            FinalPrice: FinalMoney,
            Infos: listIDInfos,
            Order: order._id
        }

        var dataSign ={
            User: User._id,
            Order: order._id,
            Type: BuyTicket.typePayment.toLowerCase() ?? "all",
            BuyTicket: BuyTicket._id,
            Coupon: Coupon?._id??"",
            Price: totalPrice,
            Discount: DiscountMoney,
            CouponDiscount: CouponDiscount,
            FinalPrice: FinalMoney,
            Infos: listIDInfos
        }

        await jwt.sign(dataSign, SECRET_ORDER, {expiresIn: "5m"}, (err, tokenLogin)=>{
            if(err) throw err
            return res.status(200).json({
                status: "Lấy thông tin thanh toán thành công",
                message: "Lấy thông tin thanh toán thành công",
                data: data,
                token: tokenLogin
            })
        })
    }
    catch (error) {
        console.error("OrderController - GetCheckOut: ", error);
        return res.status(500).json({
            message: "Lỗi khi lấy thông tin",
            error: error.message,
        });
    }

}

module.exports.ValidOrder = async(req, res)=>{
    var {token, secret, payment} = req.body
   

    var SECRET = secret + SECRET_ORDER_END
    try{
        await jwt.verify(token, SECRET , async (err, data)=>{
            if(err)
            {
                return res.status(400).json({
                    status: 'Invalid Token',
                    message: 'Token hoặc khóa secret không đúng hoặc hết hạn'
                })            
            }

            console.log(data);
            var {User, Type, BuyTicket, Price, Discount, CouponDiscount, FinalPrice, Order, Infos} = data
            
            var OrderRow = await OrderModel.findByIdAndUpdate(Order, {
                payment: payment,
                date: Date.now(),
                status: "done"
            })

            var BuyTicket = await BuyTicketModel.findById(BuyTicket)         



            if(Type === "all")
            {
                BuyTicket = await BuyTicketModel.findByIdAndUpdate(BuyTicket,
                    {
                        status: "done",
                        accPay: User
                    }
                )
                .populate({
                    path: 'ticketInfo', // Liên kết ticketInfo
                    // match: {
                    //     $and: [
                    //         { accPay: { $exists: false } }, // `accPay` không tồn tại
                    //         { 'ticket.accBuy': null }, // `ticket.accBuy` bằng null
                    //         { 'ticket.isValid': true } // `ticket.isValid` là true
                    //     ]
                    // },
                    populate: {
                        path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                        populate: {
                            path: 'info', // Liên kết tiếp tới info trong ticket
                            model: 'ticket_types'
                        }
                    }
                })
                .lean();

                // console.log(BuyTicket);

                
                var listInfos = BuyTicket.ticketInfo
                
                // update list info
                var listId = listInfos
                    .filter(inf => inf.accPay === null) // Lọc những phần tử có accPay = null
                    .map(inf => inf._id.toString()); // Sau đó lấy _id và chuyển sang chuỗi
                // console.log(listId);

                await TicketInfoModel.updateMany(
                    {_id: {$in: listId}},
                    {
                        status: "done",
                        accPay: User,
                        date: Date.now()
                    }
                )
                // console.log("Update: ", l);

                // update ticket in info
                var listTicketId =  listInfos
                    .filter(inf => inf.accPay === null) // Lọc những phần tử có accPay = null
                    .map((inf)=> inf.ticket._id.toString())

                await TicketModel.updateMany(
                    {
                        _id: {$in: listTicketId},
                        accBuy: null
                    },
                    {
                        accBuy: User
                    }
                )
                
            }
            else 
            {
                await TicketInfoModel.updateMany(
                    {_id: {$in: Infos}},
                    {
                        status: "done",
                        accPay: User,
                        date: Date.now()
                    }
                )
                .populate('ticket')

                var listInfo = await TicketInfoModel.find({
                    _id: {$in: Infos}
                })

                var listTicketId = listInfo.map(inf=> inf.ticket._id.toString())

                await TicketModel.updateMany(
                    {
                        _id: {$in: listTicketId},
                        accBuy: null
                    },
                    {
                        accBuy: User
                    }
                )
            }
            return res.status(200).json({
                message: "Thanh toán thành công"
            })

    
        })
    }
    catch(e)
    {
        console.log("Error at OrderController - ValidOrder: ", err);
        return res.status(500).json({
            status: "Error Server",
            message: "Thanh toán thất bại. Vui lòng thử lại sau"
        })
    }
   
}

module.exports.StripeCheckout = async (req, res) => {
    try {
        const { User, BuyTicket, Coupon, Infos } = req.vars;
        const { infos } = req.body;
        var CouponDiscount = 0
        var totalPrice = 0
        var DiscountMoney = 0
        var FinalMoney = 0

        if(BuyTicket.typePayment === "all")
        {
            // Tính tổng `price` từ `ticket_types`
            totalPrice = BuyTicket.ticketInfo.reduce((total, ticketInfo) => {
                const ticketPrice = ticketInfo.ticket?.info?.price || 0; // Lấy giá tiền, nếu không có thì là 0
                return total + ticketPrice;
            }, 0);
            DiscountMoney = BuyTicket.discount?.percentDiscount ? (BuyTicket.discount?.percentDiscount*totalPrice)/100 : 0
        }
        else if(BuyTicket.typePayment === "single")
        {
            totalPrice = Infos.reduce((acc, info) => {
                acc += info.ticket.info.price;
                return acc;
            }, 0);
        }

        if(Coupon)
        {
            CouponDiscount = Coupon.maxDiscount < 0
                ? (Coupon.percentDiscount * totalPrice) / 100
                : Math.min((Coupon.percentDiscount * totalPrice) / 100, Coupon.maxDiscount);
        }

        FinalMoney = Math.max(totalPrice - DiscountMoney - CouponDiscount, MIN_MONEY_PAY)

        var listIDInfos = []
        if(BuyTicket.typePayment === "single" && Infos && Array.isArray(Infos) && Infos.length > 0)
        {
            listIDInfos = Infos.map((info)=> info._id.toString())
        }

        var order = await OrderModel.create({
            accBuy: User._id,
            buyTicket: BuyTicket._id,
            ticketInfo: listIDInfos,
            discount: BuyTicket.discount?._id,
            coupon: Coupon?._id,
            typePayment: BuyTicket.typePayment,
            payment: "card",
            price: totalPrice,
            discountMoney: DiscountMoney,
            CouponDiscountMoney: CouponDiscount,
            priceFinal: FinalMoney
        })

        // Tạo Stripe Checkout Session
        const session = await stripe.checkout.sessions.create({
            payment_method_types: ['card'], // Hỗ trợ thanh toán qua thẻ
            mode: 'payment', // Chế độ thanh toán một lần
            success_url: `${process.env.CLIENT_URL}/checkout/payment-success?session_id={CHECKOUT_SESSION_ID}`, // URL chuyển hướng khi thanh toán thành công
            cancel_url: `${process.env.CLIENT_URL}/checkout/payment-cancel`, // URL khi người dùng hủy thanh toán
            customer_email: User.email, // Email của khách hàng
            line_items: [
                {
                    price_data: {
                        currency: 'vnd',
                        product_data: {
                            name: 'Thanh toán vé', // Tên sản phẩm
                        },
                        unit_amount: Math.round(FinalMoney ), // Giá tiền tính bằng đơn vị nhỏ nhất
                    },
                    quantity: 1, // Số lượng
                },
            ],
            metadata: {
                orderId: order._id.toString(),
                userId: User._id.toString(),
                typePayment: BuyTicket.typePayment,
            },
        });

        // Trả về URL để chuyển người dùng đến trang thanh toán
        return res.status(200).json({
            status: 'success',
            url: session.url, // URL Stripe Checkout
        });

    } catch (error) {
        console.error("StripeCheckout Error:", error);
        return res.status(500).json({
            status: 'error',
            message: 'Lỗi khi tạo thanh toán Stripe',
            error: error.message,
        });
    }
};

module.exports.StripeWebhook = async (req, res) => {
    const endpointSecret = process.env.STRIPE_WEBHOOK_SECRET;
    const sig = req.headers['stripe-signature'];

    let event;
    try {
        event = stripe.webhooks.constructEvent(req.rawBody, sig, endpointSecret);
    } catch (err) {
        console.error("Webhook Signature Error:", err.message);
        return res.status(400).send(`Webhook Error: ${err.message}`);
    }

    // Xử lý sự kiện từ Stripe
    if (event.type === 'payment_intent.succeeded') {
        const paymentIntent = event.data.object;
        const orderId = paymentIntent.metadata.orderId;

        console.log("orderId: ", orderId)
        try {
            // Cập nhật trạng thái đơn hàng
            await OrderModel.findByIdAndUpdate(orderId, {
                status: 'done',
                payment: paymentIntent.id,
                date: new Date(),
            });

            console.log(`Payment succeeded for Order: ${orderId}`);
            res.status(200).send('Success');
        } catch (error) {
            console.error("Order Update Error:", error.message);
            res.status(500).send('Internal Server Error');
        }
    } else {
        console.log(`Unhandled event type ${event.type}`);
        res.status(400).send('Event not handled');
    }
};

module.exports.StripeSuccess = async (req, res)=>{
    try {
        const sessionId = req.query.session_id;
        console.log("Session id: ", sessionId)
        // Truy vấn chi tiết session từ Stripe
        const session = await stripe.checkout.sessions.retrieve(sessionId);

        // Truy cập metadata
        const metadata = session.metadata;
        var {orderId, userId, typePayment} = metadata

        var OrderM = await OrderModel.findByIdAndUpdate(orderId,
            {
                payment: "card",
                date: Date.now(),
                status: "done"
            }
        )


        if(typePayment === "all")
        {
            BuyTicket = await BuyTicketModel.findByIdAndUpdate(BuyTicket,
                {
                    status: "done",
                    accPay: userId
                }
            )
                .populate({
                    path: 'ticketInfo', // Liên kết ticketInfo
                    populate: {
                        path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                        populate: {
                            path: 'info', // Liên kết tiếp tới info trong ticket
                            model: 'ticket_types'
                        }
                    }
                })
                .lean();

            // console.log(BuyTicket);


            var listInfos = BuyTicket.ticketInfo

            // update list info
            var listId = listInfos
                .filter(inf => inf.accPay === null) // Lọc những phần tử có accPay = null
                .map(inf => inf._id.toString()); // Sau đó lấy _id và chuyển sang chuỗi
            // console.log(listId);

            await TicketInfoModel.updateMany(
                {_id: {$in: listId}},
                {
                    status: "done",
                    accPay: userId,
                    date: Date.now()
                }
            )
            // console.log("Update: ", l);

            // update ticket in info
            var listTicketId =  listInfos
                .filter(inf => inf.accPay === null) // Lọc những phần tử có accPay = null
                .map((inf)=> inf.ticket._id.toString())

            await TicketModel.updateMany(
            {
                    _id: {$in: listTicketId},
                    accBuy: null
                 },
                {
                    accBuy: userId
                }
            )
        }
        else{
            var Infos = OrderM.ticketInfo
            if(Infos && Array.isArray(Infos) && Infos.length > 0)
            {

                 await TicketInfoModel.updateMany(
                    {
                        _id: {$in: Infos},
                        accPay: null
                    },
                    {
                        status: "done",
                        accPay: userId,
                        date: Date.now()
                    }
                )
                .populate('ticket')

                var listInfo = await TicketInfoModel.find({
                    _id: {$in: Infos}
                })

                var listTicketId = listInfo.map(inf=> inf.ticket._id.toString())

                console.log("Ticket id: ", listTicketId)

                await TicketModel.updateMany(
                    {
                        _id: {$in: listTicketId},
                        accBuy: null
                    },
                    {
                        accBuy: userId
                    }
                )
            }
        }


        res.status(200).send(`
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Thanh toán thành công</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    text-align: center;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    background-color: #f4f4f4;
                }
                .content {
                    background-color: white;
                    padding: 30px;
                    border-radius: 8px;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }
                h3 {
                    color: green;
                }
                h4 {
                    color: #555;
                }
            </style>
        </head>
        <body>
            <div class="content">
                <h3>Thanh toán thành công</h3>
                <h4>Bạn có thể đóng màn hình này!</h4>
            </div>
        </body>
        </html>
    `);

    } catch (error) {
        console.error('Error retrieving session:', error);
        res.status(500).json({ message: 'Error retrieving session data' });
    }
}

module.exports.Delete = async(req, res)=>{
    try{
        var {Info, BuyTicket} = req.vars
        if(Info)
        {
             await BuyTicketModel.findByIdAndUpdate(
                BuyTicket._id, // ID của BuyTicket
                {
                    $pull: { ticketInfo: Info._id } // Xóa ticketInfoId khỏi mảng ticketInfo
                },
                { new: true } // Trả về tài liệu đã cập nhật
            );
            await TicketInfoModel.findByIdAndDelete(Info._id)
        }
        else{
            await BuyTicketModel.findByIdAndUpdate(
                BuyTicket._id, // ID của BuyTicket
        {
                    isDeleted: true
                },
                { new: true } // Trả về tài liệu đã cập nhật
            );
        }

        return res.status(200).json({
            message: "Xóa thành công"
        })

    }
    catch (error) {
        console.error('Error OrderController Delete:', error);
        res.status(500).json({ message: 'Vui lòng thử lại sau' });
    }
}