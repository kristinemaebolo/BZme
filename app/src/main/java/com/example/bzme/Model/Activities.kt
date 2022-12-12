package com.example.bzme.Model

class Activities {
    var id : Int = 0
    var title : String = ""
    var reply : String = ""
    var from_time : String = ""
    var to_time : String = ""
    var created_at : Long = 0
    var status : Int = 0

    constructor(title: String, reply: String, from_time: String, to_time: String, created_at: Long, status: Int){
        this.title = title
        this.reply = reply
        this.from_time = from_time
        this.to_time = to_time
        this.created_at = created_at
        this.status = status
    }

    constructor()
}