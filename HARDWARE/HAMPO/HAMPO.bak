EESchema Schematic File Version 4
EELAYER 26 0
EELAYER END
$Descr USLetter 11000 8500
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L ESP32_HAMPO:ESP32-DEVKITC-32D U1
U 1 1 5F9AD22A
P 4800 4250
F 0 "U1" H 4800 5417 50  0000 C CNN
F 1 "ESP32-DEVKITC-32D" H 4800 5326 50  0000 C CNN
F 2 "" H 4800 4250 50  0001 L BNN
F 3 "Espressif Systems" H 4800 4250 50  0001 L BNN
F 4 "4" H 4800 4250 50  0001 L BNN "Campo4"
	1    4800 4250
	1    0    0    -1  
$EndComp
$Comp
L Connector_Generic:Conn_01x03 S1
U 1 1 5F9AD2D2
P 1275 1375
F 0 "S1" V 1241 1187 50  0000 R CNN
F 1 "S_HumTemp" V 1375 1600 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 1275 1375 50  0001 C CNN
F 3 "~" H 1275 1375 50  0001 C CNN
	1    1275 1375
	0    -1   -1   0   
$EndComp
$Comp
L Connector_Generic:Conn_01x04 P1
U 1 1 5F9AD366
P 3550 1750
F 0 "P1" V 3750 1725 50  0000 R CNN
F 1 "Pantalla_Oled" V 4050 1950 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x04_Pitch2.54mm" H 3550 1750 50  0001 C CNN
F 3 "~" H 3550 1750 50  0001 C CNN
	1    3550 1750
	0    -1   -1   0   
$EndComp
$Comp
L Connector_Generic:Conn_01x04 S3
U 1 1 5F9AD3B0
P 4425 1400
F 0 "S3" V 4391 1112 50  0000 R CNN
F 1 "S_Iluminación" V 4525 1600 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x04_Pitch2.54mm" H 4425 1400 50  0001 C CNN
F 3 "~" H 4425 1400 50  0001 C CNN
	1    4425 1400
	0    -1   -1   0   
$EndComp
$Comp
L Connector_Generic:Conn_01x03 O1
U 1 1 5F9AD43B
P 2525 1375
F 0 "O1" V 2491 1187 50  0000 R CNN
F 1 "Relé" V 2625 1450 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 2525 1375 50  0001 C CNN
F 3 "~" H 2525 1375 50  0001 C CNN
	1    2525 1375
	0    -1   -1   0   
$EndComp
$Comp
L Device:Buzzer BZ1
U 1 1 5F9AD48F
P 1650 2675
F 0 "BZ1" V 1702 2488 50  0000 R CNN
F 1 "Buzzer" V 1800 2525 50  0000 R CNN
F 2 "" V 1625 2775 50  0001 C CNN
F 3 "~" V 1625 2775 50  0001 C CNN
	1    1650 2675
	0    -1   -1   0   
$EndComp
$Comp
L Device:LED D1
U 1 1 5F9AD52A
P 7625 3150
F 0 "D1" V 7663 3033 50  0000 R CNN
F 1 "LED" V 7572 3033 50  0000 R CNN
F 2 "" H 7625 3150 50  0001 C CNN
F 3 "~" H 7625 3150 50  0001 C CNN
	1    7625 3150
	-1   0    0    1   
$EndComp
$Comp
L Velocista_3.0-cache1:Switch:SW_Push SW1
U 1 1 5F9AD64B
P 1550 4225
F 0 "SW1" H 1550 4510 50  0000 C CNN
F 1 "SwitchAdiestramiento" H 1550 4419 50  0000 C CNN
F 2 "" H 1550 4425 50  0001 C CNN
F 3 "" H 1550 4425 50  0001 C CNN
	1    1550 4225
	1    0    0    -1  
$EndComp
$Comp
L tinkerforge:3V3 #PWR0101
U 1 1 5F9ADE9B
P 1175 1575
F 0 "#PWR0101" H 1175 1425 50  0001 C CNN
F 1 "3V3" V 1190 1703 50  0000 L CNN
F 2 "" H 1175 1575 50  0000 C CNN
F 3 "" H 1175 1575 50  0000 C CNN
	1    1175 1575
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0102
U 1 1 5F9ADF24
P 1375 1575
F 0 "#PWR0102" H 1375 1325 50  0001 C CNN
F 1 "GND" H 1380 1402 50  0000 C CNN
F 2 "" H 1375 1575 50  0001 C CNN
F 3 "" H 1375 1575 50  0001 C CNN
	1    1375 1575
	1    0    0    -1  
$EndComp
Wire Wire Line
	1275 1575 1275 1800
Text GLabel 1275 1850 3    50   Input ~ 0
IO5
$Comp
L Connector_Generic:Conn_01x03 S2
U 1 1 5F9AE068
P 1950 1375
F 0 "S2" V 1916 1187 50  0000 R CNN
F 1 "S_PIR" V 2050 1475 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 1950 1375 50  0001 C CNN
F 3 "~" H 1950 1375 50  0001 C CNN
	1    1950 1375
	0    -1   -1   0   
$EndComp
$Comp
L tinkerforge:3V3 #PWR0103
U 1 1 5F9AE06E
P 1850 1575
F 0 "#PWR0103" H 1850 1425 50  0001 C CNN
F 1 "3V3" V 1865 1703 50  0000 L CNN
F 2 "" H 1850 1575 50  0000 C CNN
F 3 "" H 1850 1575 50  0000 C CNN
	1    1850 1575
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0104
U 1 1 5F9AE074
P 2050 1575
F 0 "#PWR0104" H 2050 1325 50  0001 C CNN
F 1 "GND" H 2055 1402 50  0000 C CNN
F 2 "" H 2050 1575 50  0001 C CNN
F 3 "" H 2050 1575 50  0001 C CNN
	1    2050 1575
	1    0    0    -1  
$EndComp
Wire Wire Line
	1950 1575 1950 1800
Text GLabel 1950 1850 3    50   Input ~ 0
IO25
Wire Wire Line
	2425 1575 2425 1800
Text GLabel 2425 1850 3    50   Input ~ 0
IO32
$Comp
L power:GND #PWR0105
U 1 1 5F9AE1D5
P 2525 1575
F 0 "#PWR0105" H 2525 1325 50  0001 C CNN
F 1 "GND" H 2530 1402 50  0000 C CNN
F 2 "" H 2525 1575 50  0001 C CNN
F 3 "" H 2525 1575 50  0001 C CNN
	1    2525 1575
	1    0    0    -1  
$EndComp
Wire Notes Line
	3950 1800 3950 1325
Wire Notes Line
	3275 1325 3275 1800
Wire Notes Line
	3275 1800 3950 1800
Wire Notes Line
	3950 1325 3275 1325
Wire Notes Line
	3925 1675 3300 1675
Wire Notes Line
	3300 1675 3300 1425
Wire Notes Line
	3300 1425 3925 1425
Wire Notes Line
	3925 1425 3925 1675
$Comp
L power:GND #PWR0106
U 1 1 5F9AE70F
P 3750 1950
F 0 "#PWR0106" H 3750 1700 50  0001 C CNN
F 1 "GND" V 3755 1822 50  0000 R CNN
F 2 "" H 3750 1950 50  0001 C CNN
F 3 "" H 3750 1950 50  0001 C CNN
	1    3750 1950
	0    -1   -1   0   
$EndComp
$Comp
L tinkerforge:3V3 #PWR0107
U 1 1 5F9AE9E7
P 3650 1950
F 0 "#PWR0107" H 3650 1800 50  0001 C CNN
F 1 "3V3" H 3665 2123 50  0000 C CNN
F 2 "" H 3650 1950 50  0000 C CNN
F 3 "" H 3650 1950 50  0000 C CNN
	1    3650 1950
	-1   0    0    1   
$EndComp
Wire Wire Line
	3550 1950 3550 2175
Text GLabel 3550 2225 3    50   Input ~ 0
IO22
Wire Wire Line
	3450 1950 3450 2150
Text GLabel 3450 2225 3    50   Input ~ 0
IO21
Wire Wire Line
	4325 1600 4325 1825
Text GLabel 4325 1875 3    50   Input ~ 0
IO15
NoConn ~ 4425 1600
$Comp
L power:GND #PWR0108
U 1 1 5F9AF076
P 4525 1600
F 0 "#PWR0108" H 4525 1350 50  0001 C CNN
F 1 "GND" H 4530 1427 50  0000 C CNN
F 2 "" H 4525 1600 50  0001 C CNN
F 3 "" H 4525 1600 50  0001 C CNN
	1    4525 1600
	1    0    0    -1  
$EndComp
$Comp
L tinkerforge:3V3 #PWR0109
U 1 1 5F9AF07C
P 4625 1600
F 0 "#PWR0109" H 4625 1450 50  0001 C CNN
F 1 "3V3" V 4640 1728 50  0000 L CNN
F 2 "" H 4625 1600 50  0000 C CNN
F 3 "" H 4625 1600 50  0000 C CNN
	1    4625 1600
	0    1    1    0   
$EndComp
Wire Wire Line
	1550 2775 1550 2950
Text GLabel 1550 3050 3    50   Input ~ 0
IO4
$Comp
L power:GND #PWR0110
U 1 1 5F9AF7B8
P 1750 2775
F 0 "#PWR0110" H 1750 2525 50  0001 C CNN
F 1 "GND" V 1755 2647 50  0000 R CNN
F 2 "" H 1750 2775 50  0001 C CNN
F 3 "" H 1750 2775 50  0001 C CNN
	1    1750 2775
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0111
U 1 1 5F9AFBE1
P 7450 1950
F 0 "#PWR0111" H 7450 1700 50  0001 C CNN
F 1 "GND" H 7455 1777 50  0000 C CNN
F 2 "" H 7450 1950 50  0001 C CNN
F 3 "" H 7450 1950 50  0001 C CNN
	1    7450 1950
	1    0    0    -1  
$EndComp
$Comp
L Device:R R2
U 1 1 5F9AFD37
P 7325 3150
F 0 "R2" H 7150 3150 50  0000 L CNN
F 1 "220" V 7325 3075 50  0000 L CNN
F 2 "Resistors_SMD:R_0805" V 7255 3150 50  0001 C CNN
F 3 "~" H 7325 3150 50  0001 C CNN
	1    7325 3150
	0    -1   -1   0   
$EndComp
Text GLabel 7175 3050 1    50   Input ~ 0
IO23
$Comp
L tinkerforge:3V3 #PWR0112
U 1 1 5F9B0661
P 1350 4225
F 0 "#PWR0112" H 1350 4075 50  0001 C CNN
F 1 "3V3" V 1365 4353 50  0000 L CNN
F 2 "" H 1350 4225 50  0000 C CNN
F 3 "" H 1350 4225 50  0000 C CNN
	1    1350 4225
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0113
U 1 1 5F9B086C
P 2075 4225
F 0 "#PWR0113" H 2075 3975 50  0001 C CNN
F 1 "GND" H 2080 4052 50  0000 C CNN
F 2 "" H 2075 4225 50  0001 C CNN
F 3 "" H 2075 4225 50  0001 C CNN
	1    2075 4225
	0    -1   -1   0   
$EndComp
$Comp
L Device:R R1
U 1 1 5F9B095C
P 1925 4225
F 0 "R1" V 1825 4175 50  0000 L CNN
F 1 "10k" V 1925 4150 50  0000 L CNN
F 2 "Resistors_SMD:R_0805" V 1855 4225 50  0001 C CNN
F 3 "~" H 1925 4225 50  0001 C CNN
	1    1925 4225
	0    -1   -1   0   
$EndComp
Wire Wire Line
	1775 4225 1750 4225
Wire Wire Line
	1750 4225 1750 4325
Connection ~ 1750 4225
Text GLabel 1750 4325 3    50   Input ~ 0
IO26
$Comp
L Velocista_3.0-cache1:Switch:SW_Push SW2
U 1 1 5F9B1270
P 7100 4900
F 0 "SW2" H 7100 5185 50  0000 C CNN
F 1 "ImanesSeguridad" H 7100 5094 50  0000 C CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x02_Pitch2.54mm" H 7100 5100 50  0001 C CNN
F 3 "" H 7100 5100 50  0001 C CNN
	1    7100 4900
	1    0    0    -1  
$EndComp
$Comp
L tinkerforge:3V3 #PWR0114
U 1 1 5F9B1276
P 6900 4900
F 0 "#PWR0114" H 6900 4750 50  0001 C CNN
F 1 "3V3" V 6915 5028 50  0000 L CNN
F 2 "" H 6900 4900 50  0000 C CNN
F 3 "" H 6900 4900 50  0000 C CNN
	1    6900 4900
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0115
U 1 1 5F9B127C
P 7625 4900
F 0 "#PWR0115" H 7625 4650 50  0001 C CNN
F 1 "GND" H 7630 4727 50  0000 C CNN
F 2 "" H 7625 4900 50  0001 C CNN
F 3 "" H 7625 4900 50  0001 C CNN
	1    7625 4900
	0    -1   -1   0   
$EndComp
$Comp
L Device:R R3
U 1 1 5F9B1282
P 7475 4900
F 0 "R3" V 7375 4850 50  0000 L CNN
F 1 "10k" V 7475 4825 50  0000 L CNN
F 2 "Resistors_SMD:R_0805" V 7405 4900 50  0001 C CNN
F 3 "~" H 7475 4900 50  0001 C CNN
	1    7475 4900
	0    -1   -1   0   
$EndComp
Wire Wire Line
	7325 4900 7300 4900
Wire Wire Line
	7300 4900 7300 5000
Connection ~ 7300 4900
Text GLabel 7300 5000 3    50   Input ~ 0
IO18
Wire Wire Line
	5600 4850 6350 4850
Wire Wire Line
	6350 4850 6350 1825
Wire Wire Line
	6350 1825 4325 1825
Connection ~ 4325 1825
Wire Wire Line
	4325 1825 4325 1875
Wire Wire Line
	3550 2175 6000 2175
Wire Wire Line
	6000 2175 6000 3550
Wire Wire Line
	6000 3550 5600 3550
Connection ~ 3550 2175
Wire Wire Line
	3550 2175 3550 2225
Wire Wire Line
	3450 2150 3300 2150
Wire Wire Line
	3300 2150 3300 2525
Wire Wire Line
	3300 2525 5950 2525
Wire Wire Line
	5950 2525 5950 3850
Wire Wire Line
	5950 3850 5600 3850
Connection ~ 3450 2150
Wire Wire Line
	3450 2150 3450 2225
Wire Wire Line
	2425 1800 3175 1800
Wire Wire Line
	3175 1800 3175 3950
Wire Wire Line
	3175 3950 4000 3950
Connection ~ 2425 1800
Wire Wire Line
	2425 1800 2425 1850
Wire Wire Line
	1950 1800 2275 1800
Wire Wire Line
	2275 1800 2275 2175
Wire Wire Line
	2275 2175 3075 2175
Wire Wire Line
	3075 2175 3075 4050
Wire Wire Line
	3075 4050 4000 4050
Connection ~ 1950 1800
Wire Wire Line
	1950 1800 1950 1850
Wire Wire Line
	1275 1800 1775 1800
Wire Wire Line
	1775 1800 1775 2350
Wire Wire Line
	1775 2350 2975 2350
Wire Wire Line
	2975 2350 2975 5425
Wire Wire Line
	2975 5425 5975 5425
Wire Wire Line
	5975 5425 5975 4250
Wire Wire Line
	5975 4250 5600 4250
Connection ~ 1275 1800
Wire Wire Line
	1275 1800 1275 1850
Wire Wire Line
	1550 2950 2875 2950
Wire Wire Line
	2875 2950 2875 5500
Wire Wire Line
	2875 5500 6025 5500
Wire Wire Line
	6025 5500 6025 4550
Wire Wire Line
	6025 4550 5600 4550
Connection ~ 1550 2950
Wire Wire Line
	1550 2950 1550 3050
Wire Wire Line
	7175 3150 6575 3150
Wire Wire Line
	6575 3150 6575 3450
Wire Wire Line
	6575 3450 5600 3450
Wire Wire Line
	7175 3150 7175 3050
Wire Wire Line
	1750 4225 1750 4100
Wire Wire Line
	1750 4100 2350 4100
Wire Wire Line
	2350 4100 2350 4250
Wire Wire Line
	2350 4250 4000 4250
Wire Wire Line
	5600 4150 7300 4150
Wire Wire Line
	7300 4150 7300 4900
$Comp
L Connector_Generic:Conn_01x03 J2
U 1 1 5F9BB2DB
P 7550 1375
F 0 "J2" V 7516 1187 50  0000 R CNN
F 1 "5v" V 7425 1187 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 7550 1375 50  0001 C CNN
F 3 "~" H 7550 1375 50  0001 C CNN
	1    7550 1375
	0    -1   -1   0   
$EndComp
$Comp
L Connector_Generic:Conn_01x03 J1
U 1 1 5F9BB3C8
P 7550 975
F 0 "J1" V 7516 787 50  0000 R CNN
F 1 "3v3" V 7425 787 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 7550 975 50  0001 C CNN
F 3 "~" H 7550 975 50  0001 C CNN
	1    7550 975 
	0    -1   -1   0   
$EndComp
$Comp
L Connector_Generic:Conn_01x03 J3
U 1 1 5F9BC420
P 7550 1750
F 0 "J3" V 7516 1562 50  0000 R CNN
F 1 "GND" V 7425 1562 50  0000 R CNN
F 2 "Pin_Headers:Pin_Header_Straight_1x03_Pitch2.54mm" H 7550 1750 50  0001 C CNN
F 3 "~" H 7550 1750 50  0001 C CNN
	1    7550 1750
	0    -1   -1   0   
$EndComp
Wire Wire Line
	7450 1950 7550 1950
Connection ~ 7450 1950
Wire Wire Line
	7550 1950 7650 1950
Connection ~ 7550 1950
Wire Wire Line
	7650 1575 7550 1575
Wire Wire Line
	7550 1575 7450 1575
Connection ~ 7550 1575
Wire Wire Line
	7450 1175 7550 1175
Wire Wire Line
	7650 1175 7550 1175
Connection ~ 7550 1175
$Comp
L tinkerforge:3V3 #PWR0116
U 1 1 5F9C6E9B
P 7450 1175
F 0 "#PWR0116" H 7450 1025 50  0001 C CNN
F 1 "3V3" V 7465 1303 50  0000 L CNN
F 2 "" H 7450 1175 50  0000 C CNN
F 3 "" H 7450 1175 50  0000 C CNN
	1    7450 1175
	0    -1   -1   0   
$EndComp
Connection ~ 7450 1175
$Comp
L tinkerforge:5V #PWR0117
U 1 1 5F9C6F14
P 2625 1575
F 0 "#PWR0117" H 2625 1675 40  0001 C CNN
F 1 "5V" V 2634 1693 40  0000 L CNN
F 2 "" H 2625 1575 60  0000 C CNN
F 3 "" H 2625 1575 60  0000 C CNN
	1    2625 1575
	0    1    1    0   
$EndComp
$Comp
L tinkerforge:5V #PWR0118
U 1 1 5F9CB960
P 7450 1575
F 0 "#PWR0118" H 7450 1675 40  0001 C CNN
F 1 "5V" V 7459 1693 40  0000 L CNN
F 2 "" H 7450 1575 60  0000 C CNN
F 3 "" H 7450 1575 60  0000 C CNN
	1    7450 1575
	0    -1   -1   0   
$EndComp
Connection ~ 7450 1575
Text Notes 7350 800  0    50   ~ 0
Alimentación\n
$Comp
L power:GND #PWR0119
U 1 1 5F9D040E
P 7775 3150
F 0 "#PWR0119" H 7775 2900 50  0001 C CNN
F 1 "GND" H 7780 2977 50  0000 C CNN
F 2 "" H 7775 3150 50  0001 C CNN
F 3 "" H 7775 3150 50  0001 C CNN
	1    7775 3150
	0    -1   -1   0   
$EndComp
$Comp
L tinkerforge:5V #PWR0120
U 1 1 5F9D1724
P 4000 5150
F 0 "#PWR0120" H 4000 5250 40  0001 C CNN
F 1 "5V" V 4009 5268 40  0000 L CNN
F 2 "" H 4000 5150 60  0000 C CNN
F 3 "" H 4000 5150 60  0000 C CNN
	1    4000 5150
	0    -1   -1   0   
$EndComp
$Comp
L tinkerforge:3V3 #PWR0121
U 1 1 5F9D297A
P 4000 3350
F 0 "#PWR0121" H 4000 3200 50  0001 C CNN
F 1 "3V3" V 4015 3478 50  0000 L CNN
F 2 "" H 4000 3350 50  0000 C CNN
F 3 "" H 4000 3350 50  0000 C CNN
	1    4000 3350
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0122
U 1 1 5F9D3BC0
P 5600 3350
F 0 "#PWR0122" H 5600 3100 50  0001 C CNN
F 1 "GND" H 5605 3177 50  0000 C CNN
F 2 "" H 5600 3350 50  0001 C CNN
F 3 "" H 5600 3350 50  0001 C CNN
	1    5600 3350
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0123
U 1 1 5F9D4DF6
P 5600 3950
F 0 "#PWR0123" H 5600 3700 50  0001 C CNN
F 1 "GND" H 5605 3777 50  0000 C CNN
F 2 "" H 5600 3950 50  0001 C CNN
F 3 "" H 5600 3950 50  0001 C CNN
	1    5600 3950
	0    -1   -1   0   
$EndComp
$Comp
L power:GND #PWR0124
U 1 1 5F9D602C
P 4000 4650
F 0 "#PWR0124" H 4000 4400 50  0001 C CNN
F 1 "GND" V 4005 4522 50  0000 R CNN
F 2 "" H 4000 4650 50  0001 C CNN
F 3 "" H 4000 4650 50  0001 C CNN
	1    4000 4650
	0    1    1    0   
$EndComp
$Comp
L Connector:Raspberry_Pi_2_3 U2
U 1 1 5F9DA9F7
P 9175 4275
F 0 "U2" H 9625 5675 50  0000 C CNN
F 1 "Raspberry_Pi_2_3" H 9750 5550 50  0000 C CNN
F 2 "Raspberry:MODULE_RASPBERRY_PI_3_MODEL_B+" H 9175 4275 50  0001 C CNN
F 3 "https://www.raspberrypi.org/documentation/hardware/raspberrypi/schematics/rpi_SCH_3bplus_1p0_reduced.pdf" H 9175 4275 50  0001 C CNN
	1    9175 4275
	1    0    0    -1  
$EndComp
Connection ~ 7175 3150
Wire Wire Line
	5600 3650 7425 3650
Wire Wire Line
	7425 3650 7425 3475
Wire Wire Line
	7425 3475 8375 3475
Wire Wire Line
	8375 3375 6825 3375
Wire Wire Line
	6825 3375 6825 3750
Wire Wire Line
	6825 3750 5600 3750
Wire Wire Line
	9475 5575 9375 5575
Connection ~ 8875 5575
Wire Wire Line
	8875 5575 8775 5575
Connection ~ 8975 5575
Wire Wire Line
	8975 5575 8875 5575
Connection ~ 9075 5575
Wire Wire Line
	9075 5575 8975 5575
Connection ~ 9175 5575
Wire Wire Line
	9175 5575 9075 5575
Connection ~ 9275 5575
Wire Wire Line
	9275 5575 9175 5575
Connection ~ 9375 5575
Wire Wire Line
	9375 5575 9275 5575
$Comp
L power:GND #PWR0125
U 1 1 5F9E9233
P 9475 5575
F 0 "#PWR0125" H 9475 5325 50  0001 C CNN
F 1 "GND" H 9480 5402 50  0000 C CNN
F 2 "" H 9475 5575 50  0001 C CNN
F 3 "" H 9475 5575 50  0001 C CNN
	1    9475 5575
	1    0    0    -1  
$EndComp
Connection ~ 9475 5575
Wire Notes Line
	6925 575  8150 575 
Wire Notes Line
	8150 575  8150 2350
Wire Notes Line
	8150 2350 6925 2350
Wire Notes Line
	6925 2350 6925 600 
$Comp
L tinkerforge:3V3 #PWR0126
U 1 1 5F9EDC68
P 9375 2975
F 0 "#PWR0126" H 9375 2825 50  0001 C CNN
F 1 "3V3" H 9390 3148 50  0000 C CNN
F 2 "" H 9375 2975 50  0000 C CNN
F 3 "" H 9375 2975 50  0000 C CNN
	1    9375 2975
	1    0    0    -1  
$EndComp
Text Notes 5300 1475 0    50   ~ 0
FALTA MEDIDOR DE AGUA \nFALTA MEDIDOR DE COMIDA
$EndSCHEMATC
