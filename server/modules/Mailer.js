const { google } = require("googleapis");
const nodemailer = require("nodemailer");


const CLIENT_ID = process.env.MAIL_CLIENT_ID || "";
const CLIENT_SECRET =process.env.MAIL_CLIENT_SECRET || "";
const REFRESH_TOKEN =process.env.MAIL_REFRESH_TOKEN || "";
const REDIRECT_URI =process.env.MAIL_REDIRECT_URI || "";
const MY_EMAIL =process.env.MAIL_MY_EMAIL || "";

const oAuth2Client = new google.auth.OAuth2(
    CLIENT_ID,
    CLIENT_SECRET,
    REDIRECT_URI
);

oAuth2Client.setCredentials({ refresh_token: REFRESH_TOKEN });


const sendEmail = async (toEmail, subject, html) => {
    const ACCESS_TOKEN = await oAuth2Client.getAccessToken();

    const transport = nodemailer.createTransport({
        service: "gmail",
        auth: {
            type: "OAuth2",
            user: MY_EMAIL,
            clientId: CLIENT_ID,
            clientSecret: CLIENT_SECRET,
            refreshToken: REFRESH_TOKEN,
            accessToken: ACCESS_TOKEN,
        },
        tls: {
            rejectUnauthorized: false,
        },
    });


    const from = "Ticket Booking";

    return new Promise((resolve, reject) => {
        transport.sendMail({ from, subject, to: toEmail, html: html }, (err, info) => {
            if (err) reject(err);
            resolve(info);
        });
    });
};

module.exports = sendEmail