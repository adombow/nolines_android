![NoLines Logo](images/logoaltdark.png?raw=true "NoLines")

# NoLines

> NoLines is a virtual lineup application to help reduce the amount of time that guests spend wating in line.

One of the biggest complaints that many people have about going to theme parks is the time wasted waiting in lines.  NoLines works by virtualizing this process and allowing guests to pre-select their desired ride times.  In doing so, guests can then be notified when their ride time is approaching which allows them to partake in other activities while they wait, improving not only guest happiness but also the park's revenue.  By reducing time spent in lines, guests will have more time to enjoy the rest of the park's offerings, including purchasing gifts, having a meal or even partaking in higher capacity attractions that don't require lining up.

## Features

### Home view

<img align="left" src="images/MainScreen.gif?raw=true" width="250" alt="Main Screen Overview"/>

<br><br><br><br><br><br>

* Immediately presents the guest with calls to action to application's most important features
* Card-based view set up to allow cards to be interchanged easily
<br><br><br><br><br><br><br><br><br><br><br>
---

### Reservations

<img align="right" src="images/ReservationScreenPhoneBorder.png?raw=true" width="250" alt="Reservation Screen"/>

<br><br>

* Making a reservation
  1. Pick a date
  2. Pick a morning, afternoon or evening time frame
  3. Find your desired ride and select the time you'd like to reserve
* Each reservation is for the entire hour, guests can arrive when most convenient
<br><br><br><br><br>

<img align="left" src="images/RemoveReservations.gif?raw=true" width="250" alt="Reservation Removal"/>

<br><br><br><br><br><br><br><br>

* Limited number of people allowed per ride, per hour, handled by server to ensure no over-bookings
* Ride slots adjustable by park operators to allow crowd control based on ride capacities 
* Guests can easily cancel their existing reservations, freeing up the slot for others

<br><br><br><br><br>

---

### Map

<img align="right" src="images/ReservationDirections.gif?raw=true" width="250" alt="Directions Display"/>

<br><br><br><br>

* Directly accesible for use as a general park map
* Ride markers colour coded to represent rides for which the guest has an existing reservation
* Selecting a ride marker displays basic information about the ride, without leaving the map
* Also accesible from reservations
    * Selecting the desired ride will immediately open the map and display walking directions to the ride
<br><br><br><br><br><br><br>
---

### Notifications

* 4 notifications offered per ride reservation
  1. Prior to ride window open to allow guests time to make their reservation
  2. At ride window open
  3. Prior to ride window close, ensures guests won't miss their reservation
  4. At ride window close
* Set to fire even with app closed, ensuring guests never miss a ride
* Easily toggleable in-app or more extensivley configurable in the Android notification channel

## Contributions

#### Timothy Leung:

* Server and Datbase
* UI design
* Reservation activity
* Home screen

#### Andrew Dombowsky:

* Map activity
* Notification system
* Application data structure design

## Release History

* 1.0
  * Initial Release