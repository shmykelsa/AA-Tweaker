# AA-AIO-TWEAKER

The ultimate All-In-One Utility to tweak Android Auto behaviour.

# How do I use it?

Allow root access, choose what you want, reboot, and then forget about it :)

# How does it work?

This app uses SQLite commands to override some flags related to Android Auto into the Google Play Services. 

Google Play Services control a lot of features inside Android Auto. Some of them are core functionality of Android Auto, some of them are upcoming feature that are simply not yet released.

What this app does is making some SQLite queries in order to alter some features of Android Auto. It contains the trick inside [AA Phenotype Patcher](https://github.com/Eselter/AA-Phenotype-Patcher) to patch custom apps in order to work in AA (and the same script is contained) and with the same principle it unlocks some other things in Android Auto.

# How is this different from AA Phenotype Patcher?

It just contains more features, the project is actually a fork of AA Phenotype Patcher, and share a lot of core functionality under the hood.

# Can I use this app instead of AA Phenotype Patcher?

Yes you can, but if you have already patched apps with AA Phenotype Patcher, you can leave it like that and simply apply the other tweaks. This app won't check if the apps are already patched via AA Phenotype Patcher, so you won't have a green check status at the start. 

# How is this different from Sensible Android Auto: Xposed?

This app doesn't need Xposed, which in some cases is one more pain to have. Also, it should survive Android Auto updates (unless the flag are changed) without any user modification.

# Can I use this app instead of Sensible Android Auto: Xposed?

Yes, you can. The functionaility is the same, the only "advantage" is not having to install Xposed to make it work. This app won't check if you have SAAX installed so you won't have a green check status at the start. If you are familiar with SAAX and you want to keep it, you may not want to disable speed limitation and six tap with this app.

# What happens if I wipe Google Play Services datas?

It's best advised to clear also datas of this app, in that case :)

Credits:<br>
[Jan94](https://github.com/jan94) for the original app whitelist hack <br>
[SAAX by agentdr8](https://gitlab.com/agentdr8/saax) who inspired me for some features of this app <br>
[AA Phenotype Patcher by Eselter](https://github.com/Eselter/AA-Phenotype-Patcher) <br>

Icon made by [FreePik](http://www.freepik.com/) author: [FlatIcon](https://www.flaticon.com/)
