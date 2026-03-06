-- 修复 fin_accounts 表中的错误图标
-- Create Date: 2026-03-06

-- 更新资产类账户图标
UPDATE fin_accounts SET icon = 'material-symbols-light:account-balance-wallet-outline-rounded' WHERE id = '2020050575294844929' AND name = '资产';
UPDATE fin_accounts SET icon = 'material-symbols-light:savings-outline-rounded' WHERE id = '2020050575424868354' AND name = '流动资产';
UPDATE fin_accounts SET icon = 'material-symbols-light:paid-outline-rounded' WHERE id = '2020050575622000641' AND name = '现金';
UPDATE fin_accounts SET icon = 'material-symbols-light:chat-outline-rounded' WHERE id = '2020050575622000643' AND name = '微信';
UPDATE fin_accounts SET icon = 'material-symbols-light:show-chart-outline-rounded' WHERE id = '2020050575424868355' AND name = '投资资产';
UPDATE fin_accounts SET icon = 'material-symbols-light:receipt-long-outline-rounded' WHERE id = '2020050575424868356' AND name = '应收账款';
UPDATE fin_accounts SET icon = 'material-symbols-light:receipt-outline-rounded' WHERE id = '2020050575622000644' AND name = '公司报销款';
UPDATE fin_accounts SET icon = 'material-symbols-light:handshake-outline-rounded' WHERE id = '2020050575622000645' AND name = '借出款项';
UPDATE fin_accounts SET icon = 'material-symbols-light:home-outline-rounded' WHERE id = '2020050575424868357' AND name = '固定资产';
UPDATE fin_accounts SET icon = 'material-symbols-light:directions-car-outline-rounded' WHERE id = '2020050575622000646' AND name = '汽车';
UPDATE fin_accounts SET icon = 'material-symbols-light:lock-outline-rounded' WHERE id = '2020050575424868358' AND name = '受限资产';
UPDATE fin_accounts SET icon = 'material-symbols-light:savings-outline-rounded' WHERE id = '2020050575622000647' AND name = '公积金';

-- 更新负债类账户图标
UPDATE fin_accounts SET icon = 'material-symbols-light:credit-card-outline-rounded' WHERE id = '2020050575294844930' AND name = '负债';
UPDATE fin_accounts SET icon = 'material-symbols-light:credit-card-outline-rounded' WHERE id = '2020050575424868359' AND name = '流动负债';
UPDATE fin_accounts SET icon = 'material-symbols-light:credit-card-outline-rounded' WHERE id = '2020050575622000648' AND name = '花呗';
UPDATE fin_accounts SET icon = 'material-symbols-light:account-balance-outline-rounded' WHERE id = '2020050575424868360' AND name = '长期负债';
UPDATE fin_accounts SET icon = 'material-symbols-light:directions-car-outline-rounded' WHERE id = '2020050575622000649' AND name = '车贷';
UPDATE fin_accounts SET icon = 'material-symbols-light:home-outline-rounded' WHERE id = '2020050575622000650' AND name = '房贷';

-- 更新权益类账户图标
UPDATE fin_accounts SET icon = 'material-symbols-light:trending-up-outline-rounded' WHERE id = '2020050575294844931' AND name = '权益';
UPDATE fin_accounts SET icon = 'material-symbols-light:analytics-outline-rounded' WHERE id = '2020050575424868361' AND name = '期初权益';
UPDATE fin_accounts SET icon = 'material-symbols-light:balance-outline-rounded' WHERE id = '2020050575424868362' AND name = '余额调整';

-- 更新收入类账户图标
UPDATE fin_accounts SET icon = 'material-symbols-light:attach-money-outline-rounded' WHERE id = '2020050575294844932' AND name = '收入';
UPDATE fin_accounts SET icon = 'material-symbols-light:work-outline-rounded' WHERE id = '2020050575424868363' AND name = '主动收入';
UPDATE fin_accounts SET icon = 'material-symbols-light:payments-outline-rounded' WHERE id = '2020050575622000651' AND name = '工资';
UPDATE fin_accounts SET icon = 'material-symbols-light:card-giftcard-outline-rounded' WHERE id = '2020050575622000652' AND name = '奖金';
UPDATE fin_accounts SET icon = 'material-symbols-light:account-balance-outline-rounded' WHERE id = '2020050575424868364' AND name LIKE '被动收入%';
UPDATE fin_accounts SET icon = 'material-symbols-light:percent-outline-rounded' WHERE id = '2020050575622000653' AND name = '利息';
UPDATE fin_accounts SET icon = 'material-symbols-light:trending-up-outline-rounded' WHERE id = '2020050575622000654' AND name = '股息';
UPDATE fin_accounts SET icon = 'material-symbols-light:swap-horiz-outline-rounded' WHERE id = '2020050575622000655' AND name = '二手交易';

-- 更新支出类账户图标
UPDATE fin_accounts SET icon = 'material-symbols-light:payments-outline-rounded' WHERE id = '2020050575294844933' AND name = '支出';
UPDATE fin_accounts SET icon = 'material-symbols-light:restaurant-outline-rounded' WHERE id = '2020050575424868365' AND name = '餐饮';
UPDATE fin_accounts SET icon = 'material-symbols-light:grocery-outline-rounded' WHERE id = '2020050575622000656' AND name = '买菜生鲜';
UPDATE fin_accounts SET icon = 'material-symbols-light:ramen-dining-outline-rounded' WHERE id = '2020050575622000657' AND name = '一日三餐';
UPDATE fin_accounts SET icon = 'material-symbols-light:icecream-outline-rounded' WHERE id = '2020050575622000658' AND name = '零食饮料';
UPDATE fin_accounts SET icon = 'material-symbols-light:directions-car-outline-rounded' WHERE id = '2020050575424868366' AND name = '日常交通';
UPDATE fin_accounts SET icon = 'material-symbols-light:train-outline-rounded' WHERE id = '2020050575622000659' AND name LIKE '公共交通%';
UPDATE fin_accounts SET icon = 'material-symbols-light:local-taxi-outline-rounded' WHERE id = '2020050575622000660' AND name = '打车';
UPDATE fin_accounts SET icon = 'material-symbols-light:local-gas-station-outline-rounded' WHERE id = '2020050575622000661' AND name = '车辆日常';
UPDATE fin_accounts SET icon = 'material-symbols-light:build-outline-rounded' WHERE id = '2020050575622000662' AND name = '车辆养护';
UPDATE fin_accounts SET icon = 'material-symbols-light:home-outline-rounded' WHERE id = '2020050575424868367' AND name = '居住';
UPDATE fin_accounts SET icon = 'material-symbols-light:key-outline-rounded' WHERE id = '2020050575622000663' AND name = '房租';
UPDATE fin_accounts SET icon = 'material-symbols-light:light-bulb-outline-rounded' WHERE id = '2020050575622000664' AND name = '水电网';
UPDATE fin_accounts SET icon = 'material-symbols-light:shopping-bag-outline-rounded' WHERE id = '2020050575424868368' AND name = '购物';
UPDATE fin_accounts SET icon = 'material-symbols-light:devices-outline-rounded' WHERE id = '2020050575622000665' AND name = '数码电子';
UPDATE fin_accounts SET icon = 'material-symbols-light:checkroom-outline-rounded' WHERE id = '2020050575622000666' AND name = '服饰';
UPDATE fin_accounts SET icon = 'material-symbols-light:shopping-basket-outline-rounded' WHERE id = '2020050575622000667' AND name = '日用百货';
UPDATE fin_accounts SET icon = 'material-symbols-light:subscriptions-outline-rounded' WHERE id = '2020050575424868369' AND name = '服务与订阅';
UPDATE fin_accounts SET icon = 'material-symbols-light:apps-outline-rounded' WHERE id = '2020050575622000668' AND name = '软件订阅';
UPDATE fin_accounts SET icon = 'material-symbols-light:phone-enabled-outline-rounded' WHERE id = '2020050575622000669' AND name = '手机话费';
UPDATE fin_accounts SET icon = 'material-symbols-light:medical-services-outline-rounded' WHERE id = '2020050575424868370' AND name = '医疗';
UPDATE fin_accounts SET icon = 'material-symbols-light:local-hospital-outline-rounded' WHERE id = '2020050575622000670' AND name = '看病';
UPDATE fin_accounts SET icon = 'material-symbols-light:medication-outline-rounded' WHERE id = '2020050575622000671' AND name = '药品';
UPDATE fin_accounts SET icon = 'material-symbols-light:school-outline-rounded' WHERE id = '2020050575424868371' AND name = '个人提升';
UPDATE fin_accounts SET icon = 'material-symbols-light:menu-book-outline-rounded' WHERE id = '2020050575622000672' AND name LIKE '书籍%';
UPDATE fin_accounts SET icon = 'material-symbols-light:flight-outline-rounded' WHERE id = '2020050575424868372' AND name = '差旅与度假';
UPDATE fin_accounts SET icon = 'material-symbols-light:train-outline-rounded' WHERE id = '2020050575622000673' AND name = '交通';
UPDATE fin_accounts SET icon = 'material-symbols-light:hotel-outline-rounded' WHERE id = '2020050575622000674' AND name = '酒店住宿';
UPDATE fin_accounts SET icon = 'material-symbols-light:attractions-outline-rounded' WHERE id = '2020050575622000675' AND name = '景点玩乐';
UPDATE fin_accounts SET icon = 'material-symbols-light:beach-access-outline-rounded' WHERE id = '2020050575622000676' AND name = '度假消费';
UPDATE fin_accounts SET icon = 'material-symbols-light:favorite-outline-rounded' WHERE id = '2020050575424868373' AND name = '情感与社交';
UPDATE fin_accounts SET icon = 'material-symbols-light:favorite-outline-rounded' WHERE id = '2020050575622000677' AND name = '伴侣投入';
UPDATE fin_accounts SET icon = 'material-symbols-light:elderly-outline-rounded' WHERE id = '2020050575622000678' AND name = '孝敬长辈';
UPDATE fin_accounts SET icon = 'material-symbols-light:diversity-3-outline-rounded' WHERE id = '2020050575622000679' AND name = '朋友人情';
UPDATE fin_accounts SET icon = 'material-symbols-light:trending-down-outline-rounded' WHERE id = '2020050575424868374' AND name = '折旧与摊销';
UPDATE fin_accounts SET icon = 'material-symbols-light:trending-down-outline-rounded' WHERE id = '2020050575622000680' AND name = '汽车折旧';

-- 验证更新结果
SELECT
    account_type,
    COUNT(*) as total,
    COUNT(CASE WHEN icon = 'material-symbols-light:account-balance-wallet-outline-rounded' THEN 1 END) as wallet_icons
FROM fin_accounts
WHERE id LIKE '20200505%'
GROUP BY account_type;
