![Kin Token](kin_android.png)
# Overview
This repository contains a module which connects all the ecosystem apps together.


## Ecosystem Apps Discovery Module
This Module provides the ability to users to explore other apps in the Kin Ecosystem and get familiar with the different experiences users can enjoy using Kin
It also allows users to send/receive kin to/from different ecosystem applications.


## Installation
To include the library in your project add jitpack to your *build.gradle* project file.

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the latest release appsDiscovery version to your *build.gradle* file


```gradle
dependencies {
    ...
    implementation 'com.github.kinecosystem.move-kin-android:appsDiscovery::<latest release>'
}
```

For the latest release version go to [https://github.com/kinecosystem/move-kin-android/releases](https://github.com/kinecosystem/move-kin-android/releases).



## Explore the Ecosystem

- Adding a button that starts the experience of exploring the Ecosystem - button *AppsDiscoveryButton* 

You can embed the button in any of your layout xml files
click on the button starts the exploring experience 

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout>
    ...
    <org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent" 
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"/>

</android.support.constraint.ConstraintLayout>
```

- Showing explore dialog to the user - *AppsDiscoveryAlertDialog* 

As a trigger to some of a user's actions, you can create and display a dialog *AppsDiscoveryAlertDialog* to the user  
The user can then choose to start exploring the Ecosystem

```java
    AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(context);
    dialog.show();
```

You can create any button that follows your application style and on a click of a button, it starts the whole experience
by starting an activity *AppsDiscoveryActivity* 

```java
someButton.setOnClickListener(v -> {
            Intent intent = AppsDiscoveryActivity.Companion.getIntent(context)
             startActivity(intent);
        });
```

## Design & UX

For design & UX recommendations and tips on the way to give your users the best experience please follow  [https://discover.kin.org/ux_guidlines_v1.pdf](https://discover.kin.org/ux_guidlines_v1.pdf).


## Send Kin
In order for your app to be able to send Kin

- In your root project, add a new package named *kindiscover*
- In kindiscover directory - create a service class named *SendKinService*, declare it in a *manifest.xml* file
and configure the service as not to be exported

```xml
<application>
    ...
    <service android:name=".kindiscover.SendKinService"
             android:exported="false" />
</application>
```

**Notice!** it is important to define the service as **not to be exported**



The *SendKinService* class must extends the abstract service *SendKinServiceBase*
and implement the two abstract methods:
- *transferKin* 
- *getCurrentBalance* 


```java
public class SendKinService extends SendKinServiceBase {
    @NonNull
    @Override
    public KinTransferComplete transferKin(@NonNull String toAddress, int amount, @NonNull String memo) throws KinTransferException {
       
        //perform the transfer with the given parameters
       
        //if transfer fails throw KinTransferException
          throw new KinTransferException("sender address", "transfer error details");
          //if transfer completes
        return new KinTransferComplete("sender address", "transaction id", "transaction memo");
    }

    @Override
    public BigDecimal getCurrentBalance() throws BalanceException {
        
        //get balance from the wallet
       
       double balance = ...getBalance();
       
       //if fails to get balance throw BalanceException
         throw new BalanceException("error details");
         //else return the balance
       return new BigDecimal(balance);
    }
}
```

These methods are asynchronous and can perform long operations



## Receive Kin
In order for an app to be able to receive Kin from other apps

- In your root project add new package named *kindiscover*
- In the kindiscover directory - create an activity class *AccountInfoActivity* and declare it in *manifest.xml* file


```xml
<application>
    ...
    <activity android:name=".kindiscover.AccountInfoActivity"
              android:exported="true"/>       
</application>
```
Very important to define the activity to be exported

The *AccountInfoActivity* class must extends the abstract activity *AccountInfoActivityBase*
and implement the abstract method:
- *getPublicAddress* 


```java
public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getPublicAddress() {
        // return the user a public address from the wallet
        return "some address";
    }
    
}
```
This method is asynchronous and can perform long operations


## Receive other apps transfer notifications 
In order to get notified about other applications that try to send kin to your app


- In your root project add new package named *Kindiscover*
- In the kindiscover directory - create a service class named *ReceiveKinService*, declare it in *manifest.xml* file
and configure the service to be exported

```xml
<application>
    ...
    <service android:name=".kindiscover.ReceiveKinService"
             android:exported="true" />
</application>
```

**Notice!** Very important to define the service to be **exported**



The *ReceiveKinService* class must extends the abstract service *ReceiveKinServiceBase*
and implement the two abstract methods:
- *onTransactionCompleted* 
- *onTransactionFailed* 


```java
public class ReceiveKinService extends ReceiveKinServiceBase {

    public ReceiveKinService() {
        super();
    }

    @Override
    public void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) {
        //This will be called when any app complete transfer Kin to your app
    }

    @Override
    public void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String memo) {
        //This will be called when any app failed to transfer Kin to your app
    }
    
}
```


These methods are asynchronous and can perform long operations inside them

These methods are called when transfer completes or when it fails. 
When an app gets a notification of a completed transfer, it can call its local server to verify this transaction info on the blockchain, and after the transaction is verified on the blockchain
it can add an entry in the app transactions history DataBase of that transaction.




## Sample Code
You can look at the sample app for the module usage

+ [Sender Sample App](https://github.com/kinecosystem/move-kin-android/tree/master/senderSampleApp/) demonstrates
How to implement the discovery of other ecosystem apps and how to send Kin to another app.
+ [Receiver Sample App](https://github.com/kinecosystem/move-kin-android/tree/master/receiverSampleApp/) demonstrates how to implement receiving Kin in your app from another ecosystem app and how to get notified about it.
