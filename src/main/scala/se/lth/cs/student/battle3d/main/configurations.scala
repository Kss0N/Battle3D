package se.lth.cs.student.battle3d.main

object configurations:
    private var myConfigs: Map[String, String] = null

    def apply(key: String): String = 
        if myConfigs.contains(key) then 
            myConfigs(key)
        else
            throw new IllegalArgumentException(s"Key $key is not a valid config")
