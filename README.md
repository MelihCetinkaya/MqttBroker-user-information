This project allows us to listen to the data that esp32 broadcasts to mqtt broker from the frontend and backend. Frontend listens to the data instantly and transmits it to our mobile application, that is, the user. Backend listens in the same way, but to detect the highest values ​​and save them to the database. It broadcasts these highest temperature and humidity values ​​to mqtt broker and frontend receives this data. Spring boot was used for the backend and kotlin was used for the frontend.
