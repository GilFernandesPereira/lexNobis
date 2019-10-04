let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/ProjetoSolidao/{userID}/{estado}').onWrite((change,context) => {

    const receiver_id = context.params.userID;

    const deviceToken = admin.database().ref('/ProjetoSolidao/'+receiver_id+'/token').once('value');

  return deviceToken.then(result  => {
    const token_id = result.val();//meter token para dar
    console.log("Notificação Entidade");
    //const token_id = "e3Aqv5Cq-QM:APA91bE7boCagplTA2Zg3p9A_lU9kP_CWfEVwVe1wfY9sDg0CnExWZno5Xvf8Fn48ec68nA-EEx6EAvb2URwAp-qwC1R9-un5QDeoQTAbnnTKYkkaW8IHyk1Q-2Uo6H-XleheJIbDRv9";//meter token para dar
    const payload = {
        notification: {
            title:"Novidades em relação às missões",
            body: "Venha Verificar o que andam a fazer os seus seniores e animadores!",
            sound: "default"
        },
    };

    return admin.messaging().sendToDevice(token_id, payload);
  });
});





exports.sendNotifications = functions.database.ref('/ProjetoSolidao/{userID}/{nif}/{pushId}').onWrite((change,context) => {

  const tokenID= change.after.child('tokenAnimador').val();
  console.log(tokenID);const payload = {
      notification: {
          title:"Novidades em relação às missões",
          body: "Venha Verificar as suas missões!",
          sound: "default"
      },
  };

  return admin.messaging().sendToDevice(tokenID, payload);
});

exports.sendNotificationsWeb = functions.database.ref('/ProjetoSolidao/{userID}/{pushId}').onWrite((change,context) => {

  const receiver_id = context.params.userID;

    const deviceToken = admin.database().ref('/ProjetoSolidao/'+receiver_id+'/tokenWeb').once('value');

  return deviceToken.then(result  => {
    const token_id = result.val();//meter token para dar
    console.log("Notificação Entidade");
    //const token_id = "e3Aqv5Cq-QM:APA91bE7boCagplTA2Zg3p9A_lU9kP_CWfEVwVe1wfY9sDg0CnExWZno5Xvf8Fn48ec68nA-EEx6EAvb2URwAp-qwC1R9-un5QDeoQTAbnnTKYkkaW8IHyk1Q-2Uo6H-XleheJIbDRv9";//meter token para dar
    const payload = {
        notification: {
            title:"Novidades em relação às missões",
            body: "Venha Verificar o que andam a fazer os seus seniores e animadores!",
            sound: "default"
        },
    };

    return admin.messaging().sendToDevice(token_id, payload);
  });
});