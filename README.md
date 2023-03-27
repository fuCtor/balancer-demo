# balancer-demo

Демонстрация работы finagle loadbalancer

<!--
@startuml

frame Runner {

	(Server 1) as srv1
	(Server 2) as srv2
	(Server 3) as srv3
	(Server N) as srvN
	
	component Client as cl {
		[Load balancer] as lba
	}
	
	[Report] as report
	
	lba --> srv1: Request
	lba --> srv2: Request
	lba --> srv3: Request
	lba --> srvN: Request
	
	srv1 --> report : Count request
	srv2 --> report : Count request
	srv3 --> report : Count request
	srvN --> report : Count request
}

actor User

report --> User : Show report

@enduml
-->

![](https://www.plantuml.com/plantuml/svg/XP0zQmCn38PdwrTeR8SCkTiob43MSiE5Jg63ugYqu8zKjjEXvB_NEcAkXT2RB9xtqGVUfaoIYtS0RqAUSImXiE0tW7dOirnglNv4IfZaifvPrrWtixwnVcP3OmCOC3RwSmmSCkxSXpuriwukC-Rm7Ec4Ht8KBCkh9kv8OAxQTnZv72LFLAPIQSrnjNgQBjlWo9-5K_w3k_kulu-7NnYCJjNajX0tk8kbdYnDqVbBGhya3Fy9Lm2oEGg-91Q0cwKD2gg-VuzVxKDWo-5KlFi1)