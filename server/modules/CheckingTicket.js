const cron = require('node-cron');
const Ticket = require('../models/TicketModel')

// Định nghĩa cron job
const ticketCronJob = () => {
    cron.schedule('* * * * *', async () => { // Chạy mỗi phút
        // console.log("Running ticket cron job...");
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
    });
};

module.exports = ticketCronJob;