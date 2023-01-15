# GoldWireCoin
コンセンサスアルゴリズム:POW<br />
目標ポーリングレート:1分<br />
残額証明方式:アカウント型<br />
ノード：フルノード＋マイナー(コマンドで停止可能です)<br />
スクリプト：なし(トランザクションには署名、送金先、総金額、手数料のみが記入されます。lock-unlockスクリプトや、コントラクトのインタプリタは実装されていません)<br />
※高校生の時に作成しておりまして、かなりコードにおかしな部分が多いです。
# RECENTRY UPDATE！
 アドレスの表示を16進公開鍵→base64に<br />
<code>
ex. diDlHSAnRbhW0W/ZgaxIy3NMZotIfmvprv9knHdaYt0=§QLfieDPDeAJ8jUs3y02CrdtDd2AnSXqONb9HQiEskbQ=
</code><br />
 シードIPの問い合わせをハードコード→一般的なDNSシードと同様の構造に変更<br />
 １ブロック1ファイル保存→10ブロックごとに変更（実験中）<br />
<image style="width:840px;height=auto;" src="image/scr.png" />
