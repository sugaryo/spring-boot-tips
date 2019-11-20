/**
 * アプリケーションパッケージ.
 * 
 * <p>
 * <b>■パッケージ概要：</b><br>
 * いわゆる機能要件に基づく <b>ビジネスロジック</b> を実装するのがこのパッケージとなる。<br>
 * 論理レイヤに応じて、各サブパッケージに実装を追加していく。
 * </p>
 * <p>
 * <b>■サブパッケージ概要：</b><br>
 * app配下の各パッケージを論理レイヤごとに纏めると以下の通り。
 * <ul>
 * 	<li>【非論理レイヤ】
 * 	<ul>
 * 		<li>config
 * 		<li>api
 * 	</ul>
 * 	<li>【プレゼンテーションレイヤ】
 * 	<ul>
 * 		<li>controller.rest
 * 		<li>controller.view
 * 		<li>scheduler
 * 	</ul>
 * 	<li>【ビジネスロジックレイヤ】
 * 	<ul>
 * 		<li>module.core
 * 		<li>module.api
 * 		<li><b> -- (スレッド境界) -- </b>
 * 		<li>module.thread
 * 	</ul>
 * 	<li><b> -- トランザクション境界 -- </b>
 * 	<li>【トランザクションレイヤ／データレイヤ】
 * 	<ul>
 * 		<li>transact.usecase
 * 		<li>transact.service
 * 		<li>transact.dao        // DAOを使用する場合。
 * 		<li>transact.repository // SpringDataJPAを使用する場合。
 * 	</ul>
 * 	</ul>
 * </p>
 * 
 * 
 * @author sugaryo
 */
package sugaryo.springboot.tips.app;