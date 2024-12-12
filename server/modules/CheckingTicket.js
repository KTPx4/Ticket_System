const cron = require('node-cron');
const Ticket = require('../models/TicketModel')
const Order = require('../models/OrderModel')

// Định nghĩa cron job
const ticketCronJob = () => {
    cron.schedule('* * * * *', async () => { // Chạy mỗi phút
       /*
            const now = new Date();
            try {
                const tickets = await Ticket.find({
                    isAvailable: false,
                    accBuy: null,
                    expiresAt: { $lte: now }
                });

                for (const ticket of tickets) {
                    ticket.isAvailable = true; // Đặt lại trạng thái
                    ticket.expiresAt = null; // Reset thời gian hết hạn
                    await ticket.save();
                    console.log(`Ticket ${ticket._id} has been reset to available.`);
                }
            } catch (error) {
                console.error("Error in ticket cron job:", error);
            }
        */
        try {
          
            
            // Lấy thời gian hiện tại và tính 5 phút trước
            const fiveMinutesAgo = new Date(Date.now() - 5 * 60 * 1000);
    
            // Tìm và cập nhật các tài liệu
            const result = await Order.updateMany(
                { 
                    status: "waiting", 
                    createdAt: { $lte: fiveMinutesAgo } // Các tài liệu tạo trước 5 phút
                },
                { $set: { status: "destroy" } } // Cập nhật status
            );
    
            // console.log(`Updated ${result.modifiedCount} orders to status "destroy"`);
        } catch (err) {
            console.error('Error updating orders:', err);
        }

    });
};

module.exports = ticketCronJob;