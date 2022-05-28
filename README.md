LinkU
====

<img src="https://user-images.githubusercontent.com/95346303/169641708-327091bd-aa62-4e06-8262-c34bc650ed26.png" width="50">

    LinkU is a Chat application that use MVVM architeture's and Jetpack components.
    We use Room database to store all the articles, user details and conversations. 
    LiveData for observing the data change.

# Project structure
```
com.project.linku  
├── data  
│   ├── local   
|   │   ├── Dao                    # Data Access Object for Room  
|   |   ├── DataModel              # Model classes  
|   |   ├── LocalDatabase          # Query, Insert, Delete...etc command  
|   |   └── LocalRepository        # Access for Local Repository  
|   |       
│   └── remote  
|       ├── FireBaseRepository     # Remote data source  
|       ├── IFireBaseApiService    # Remote data interface  
|       └── IFireOperationCallBack # Remote data callback  
├── ui  
│   ├── home                       # View and publish the articles  
│   ├── chat                       # Chat and Conversation features  
│   ├── dashboard                  # User login and settings  
│   └── utils  
├── MainActivity                   # One Activity application  
└── MainActiviyViewModel  
```
## Main page  
<img src="https://user-images.githubusercontent.com/95346303/169640789-2834bc00-bd3d-41b6-9731-f592c1a64b65.png" width="200">

## Chat page  
<img src="https://user-images.githubusercontent.com/95346303/169640783-c629fe34-36db-45dd-86fc-a1c9196d5834.png" width="200">

## Conversation page  
<img src="https://user-images.githubusercontent.com/95346303/169640786-d6b419c3-4533-4c4f-9fc3-5b4f4328074c.png" width="200">

## Publish page  
<img src="https://user-images.githubusercontent.com/95346303/169640790-cd8d6b71-a9dd-45d7-ab18-24bfaf5dfee6.png" width="200">

## Login page  
<img src="https://user-images.githubusercontent.com/95346303/169640787-8e0ded56-4479-41f3-ba20-f56c4d5fc43f.png" width="200">
