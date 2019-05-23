![Kin Token](kin_android.png)
# Ecosystem Apps Discovery Module
This repository contains the Ecosystem Apps Discovery module. This module displays all the ecosystem's applications and enables send/receive Kin transactions between your application and other applications of the ecosystem. 


## Installation
To include the library in your project, do the following:
1. Add jitpack to your *build.gradle* project file.

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the latest appsDiscovery version to your *build.gradle* file.


```gradle
dependencies {
    ...
    implementation 'com.github.kinecosystem.move-kin-android:appsDiscovery::<latest release>'
}
```

For the latest appsDiscovery version, go to [https://github.com/kinecosystem/move-kin-android/releases](https://github.com/kinecosystem/move-kin-android/releases).



## Exploring the Ecosystem
This module can display all applications of the ecosystem on a single screen. From that screen, your user can access any one of them to get more information and to perform Kin transactions. 
We provide the following two ways of opening the screen that displays the applications: 
- Adding a button that opens the screen (*AppsDiscoveryButton*)  
- Adding a pop-up dialog that suggests opening the screen (*AppsDiscoveryAlertDialog*) with a button that opens it (*AppsDiscoveryActivity*).  
### Adding *AppsDiscoveryButton* Button  

You can embed the button in any of your layout xml files.  

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

### Adding *AppsDiscoveryAlertDialog* Pop-up Dialog  

Opening the *AppsDiscoveryAlertDialog* pop-up dialog can be triggered by some action performed by a user. The user then can choose to click the button in the dialog (*AppsDiscoveryActivity*) to open the screen that displays other applications and to start exploring the ecosystem.

```java
    AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(context);
    dialog.show();
```

For *AppsDiscoveryActivity* button in the *AppsDiscoveryAlertDialog* pop-up dialog, you can create any button that fits your application style.

```java
someButton.setOnClickListener(v -> {
            Intent intent = AppsDiscoveryActivity.Companion.getIntent(context)
             startActivity(intent);
        });
```



## Enabling Kin Transactions Between Applications
### Send Kin
In enable your app to send Kin, do the following:

1. In your root project, add a new package named *kindiscover*.
2. In the *kindiscover* directory, create a service class named *SendKinService* and declare it in the *manifest.xml* file.
3. **Important!** Configure the service not to be exported.

```xml
<application>
    ...
    <service android:name=".kindiscover.SendKinService"
             android:exported="false" />
</application>
```

4. The *SendKinService* class must extend the abstract service *SendKinServiceBase* and implement these two abstract methods:
    - *transferKin* - performs Kin transfer with given parameters
    - *getCurrentBalance* - returns the wallet's current balance


```java
public class SendKinService extends SendKinServiceBase {
    @NonNull
    @Override
    public KinTransferComplete transferKin(@NonNull String toAddress, int amount, @NonNull String memo) throws KinTransferException {
       
        
    }

    @Override
    public BigDecimal getCurrentBalance() throws BalanceException {
        
        //get balance from the wallet
       
       double balance = ...getBalance();
       
       
    }
}
```
The *transferKin* method returns new KinTransferComplete("sender address", "transaction id", "transaction memo").
If the transfer fails, an exception is thrown:
KinTransferException("sender address", "transfer error details")

The *getCurrentBalance* method returns new BigDecimal(balance). 
If it fails to get balance, an exception is thrown:
BalanceException("error details") 
    
       
These methods are asynchronous and can perform long operations.



### Receive Kin
To enable your app to receive Kin from other apps, do the following:

1. In your root project, add a new package named *kindiscover*.
2. In the kindiscover directory, create an activity class *AccountInfoActivity* and declare it in the *manifest.xml* file. 
3. **Important!** Configure the service to be exported.


```xml
<application>
    ...
    <activity android:name=".kindiscover.AccountInfoActivity"
              android:exported="true"/>       
</application>
```
4. The *AccountInfoActivity* class must extend the abstract activity *AccountInfoActivityBase* and implement the abstract method:
    - *getPublicAddress* 


```java
public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getPublicAddress() {
        
    }
    
}
```
This method returns the public address of the wallet. It is asynchronous and can perform long operations.


## Receive Transfer Notifications from Other Apps  
To get notifications from other apps when they send Kin to your app, do the following:

1. In your root project, add new package named *Kindiscover*.
2. In the kindiscover directory, create a service class named *ReceiveKinService* and declare it in the *manifest.xml* file.
3. **Important!** Configure the service to be exported.

```xml
<application>
    ...
    <service android:name=".kindiscover.ReceiveKinService"
             android:exported="true" />
</application>
```


3. The *ReceiveKinService* class must extend the abstract service *ReceiveKinServiceBase* and implement these two abstract methods:
    - *onTransactionCompleted* (called when any app completes a Kin transfer to your app)
    - *onTransactionFailed* (called when any app failed to transfer Kin to your app)


```java
public class ReceiveKinService extends ReceiveKinServiceBase {

    public ReceiveKinService() {
        super();
    }

    @Override
    public void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) {
        
    }

    @Override
    public void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String memo) {
        
    }
    
}
```

These methods are called when a transfer is complete or when it fails. 
When an app gets a notification of a completed transfer, it can call its local server to verify this transaction info on the blockchain. After the transaction is verified on the blockchain, the app can add an entry to the app transactions history database for that transaction.

These methods are asynchronous and can perform long operations.


## Design and UX

For design and UX recommendations and tips on how to give your users the best experience, see  [https://discover.kin.org/ux_guidlines_v1.pdf](https://discover.kin.org/ux_guidlines_v1.pdf).

## Sample Code
You can see sample apps for the module usage here:

+ [Sender Sample App](https://github.com/kinecosystem/move-kin-android/tree/master/senderSampleApp/) demonstrates how to implement the discovery of other ecosystem apps and how to send Kin to another app.
+ [Receiver Sample App](https://github.com/kinecosystem/move-kin-android/tree/master/receiverSampleApp/) demonstrates how to implement receiving Kin in your app from another ecosystem app and how to get notified about it.
