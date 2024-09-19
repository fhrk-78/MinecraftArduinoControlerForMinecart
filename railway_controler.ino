// 入力ピンを決めておきます
int controlerPin = A5;
int controlerValue = 0;

void setup() {
    // シリアル通信を初期化します。このコードでは速度は9600bpsで設定してます。
    Serial.begin(9600);
}

void loop() {
    // 入力を読み取ります
    controlerValue = analogRead(controlerPin);
    // 値を出力します
    Serial.println(controlerValue);
    // 1ms待機
    delay(1);
}