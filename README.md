# Invoice App (Fixed structure)

This project has the correct package layout under `com.example.invoice`.

Run:
1. mvn package
2. java -jar target/invoice-app-0.0.1-SNAPSHOT.jar
3. Open http://localhost:8080/ to access the simple frontend.

APIs:
- GET /api/dealers
- GET /api/vehicles
- POST /api/invoices  (body: {dealerId, vehicleId, customerName}) -> returns PDF
