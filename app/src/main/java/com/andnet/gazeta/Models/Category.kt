package com.andnet.gazeta.Models


class Category {

    var name: String? = null
    var backDrawbale: Int = 0
    var image: Int = 0

    constructor() {}

    constructor(name: String, image: Int, backDrawbale: Int) {
        this.name = name
        this.image = image
        this.backDrawbale = backDrawbale
    }

}
