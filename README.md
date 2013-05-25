AndroidSocialManager
====================

<b>Intro:</b>

Social platform management for android, providing basic APIs. 
NOTE that this is NOT FULLY TESTED but I'm working on that - -|||

The idea is to provide a uniformed library of different SNS platforms (Weibo, QQ, Renren, maybe more),
with basic social functions like log in/out, authorize and get user infomation (share function is still developping) 
to save efforts.

<b>Usage:</b>

To make use of this library you should:

1. Make it as reference lib of your project, coz the lib of Renren refered to some resources
2. Get an instance of SocialManager with SocialManager.getInstance()
3. Perform actions using SocialManager#performAcion(Activity, Platform, Action, Bundle, SocialActionListener)
4. Handle result in the SocialActionListener callback

Note: The project has been exported using gradle, which means you have to upgrade your ADT to ver 22.0 or later

<b>Futher:</b>
I'm planning to integrate some more platforms (like Weixin), and functions like sharing and get friends list.
This project is far far far away from good, so if you got any idea please contact me via e-mail eryx.kao@gmail.com,
your suggestions will be greatly appreciated. 
