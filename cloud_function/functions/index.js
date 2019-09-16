const functions = require('firebase-functions');
var admin = require('firebase-admin');
var app = admin.initializeApp();



// // Create and Deploy Your First Cloud Functions

function sendAlarmDataNotification() {
    var topic = 'alarm';
    var message = {
        data: {
            ignore: 'ignore'
        },
        topic: topic
    };
    admin.messaging().send(message).then((response) => {
        console.log('Successfully sent message:', response);
        return null;
    })
        .catch((error) => {
            console.log('Error sending message:', error);
        });
}

exports.sendAlarmNotification = functions.https.onRequest((req, res) => {
    sendAlarmDataNotification();
    res.send("notification send");
});

exports.scheduledFunction55Min = functions.pubsub.schedule('every 55 minutes').onRun((context) => {
    sendAlarmDataNotification();
    console.log('This will be run every 55 minutes!');
    return 0;
});