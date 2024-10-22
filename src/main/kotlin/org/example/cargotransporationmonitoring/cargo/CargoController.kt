package org.example.cargotransporationmonitoring.cargo

import com.example.api.cargo.CargoApi
import com.example.model.cargo.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CargoController: CargoApi {
    override fun apiV1CargoCargoIdGet(cargoId: Int?): ResponseEntity<Cargo> {
        TODO("Not yet implemented")
    }

    override fun apiV1CargoGet(): ResponseEntity<MutableList<Cargo>> {
        TODO("Not yet implemented")
    }

    override fun apiV1CargoLocationCargoIdGet(cargoId: Int?): ResponseEntity<MutableList<CargoLocation>> {
        TODO("Not yet implemented")
    }

    override fun apiV1CargoPost(cargoCreateRequest: CargoCreateRequest?): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }

    override fun apiV1VehiclesGet(): ResponseEntity<MutableList<Vehicle>> {
        TODO("Not yet implemented")
    }

    override fun apiV1VehiclesPost(vehicleCreateRequest: VehicleCreateRequest?): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }

    override fun apiV1VehiclesVehicleIdGet(vehicleId: Int?): ResponseEntity<Vehicle> {
        TODO("Not yet implemented")
    }

}