package com.example.test2.model.entities

data class FileData(
    var name: String,
    var downloadUrl: String
) {
    constructor() : this("", "")
}
