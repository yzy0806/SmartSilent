Comments for Instructor: Dr.Brandon

1. Pleast test this on a real phone rather than the emulator. It is such a pain to test it on a emulator.

2. There is one issue(not bug) with this app. 
If you use the plus button on the upper right corner and add/edit an  event and then you press the back button and navigate back to the app, you will find the calendar events does not get updated.
This happens because the the UI is updated on the onCreateView() function and when you navigate back from the calendar app, the app does not call the onCreateView() but just restore the previous bundle state.
In order to refresh the calendar view, you can go to the keyword view first then click back to the calendar event view then the newly edit/add events will appear.
I tried to fix this issue but the fragment lifecycle seems to be very weird and I run into some other problems in order to fix this issue. 
Because this is not a bug, but just a usability issue and the purpose of this app is to automatically silent the phone which means the plus button will be rarely used. 
I decide to leave it there and solve other problems first.

3. I did develop the put the phone face down and silent the phone feature
However, in order to achieve that the app will start a wakefulboardcast service to use gyroscope every 5 minutes and takes samples with high frquency for 10 seconds each time 
The high usage on gyroscope drain the battery very fast. In my personal experience, this app uses at least 5% of the battery capacity if I have that function.
With that high level of power consumption, this function is not useful anymore so I decide to take out that feature

4.There is bug with android system on calendarcontract with the all day events. So I have to implement a few other methods to avoid it.
The details are here https://code.google.com/p/android/issues/detail?id=71355
You will also see more details in the code comments. 

5. I do realize there are some warning about using the deprecated navigation drawer but I decide to leave it there because it is such a trouble to change it.
There are also some warning about I did not do type checking before I cast sharedprefernce into a map. That is because I know what each sharedprefernces type I put in and feel doing a type check is point less. 

6. I like this class and I did learn a lot. Maerry Christmas!

  
