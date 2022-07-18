package com.example.demo2.groovy

class Grv1 {
    static void main(String[] args) {
        def seq = [1, 2, 3, 4, 5]
        def list = seq.stream().map({ it * 2 }).collect({ it -> it.toString() }).toList()
        println list
    }
}
