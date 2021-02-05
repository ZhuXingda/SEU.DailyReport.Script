define(function(require, exports, module) {
    var utils = require('utils');
    var appConfig = require('appConfig');
    var bs = require('./dailyReportBS');
    var dailyReportSave = require('./dailyReportSave');
    var dailyReportView = require('./dailyReportView');
    var comBs = require('../../public/js/commonBS');
    var viewConfig = {
        initialize : function() {
            var view = utils.loadCompiledPage('dailyReport');
            this.$rootElement.html(view.render({
                sfxsyw : appConfig.sfxsyw
            }), true);
            this.pushSubView([ dailyReportSave ]);
            bs.curDate = dateFormat("YYYY-mm-dd", getChineseTime());
            var twsfktbData = getTwSetting();
            if (twsfktbData.success) {
                bs.sfktb1 = twsfktbData.SFKTB1;
                bs.sfktb2 = twsfktbData.SFKTB2;
            }
            this.initView();
            this.eventMap = {
                "[data-action=add]" : this.actionAdd,
                "[data-action=edit]" : this.actionEdit,
                "[data-action=detail]" : this.actionDetail,
                "[data-action=delete]" : this.actionDelete,
                "[data-action=custom-column]" : this.actionCustomColumn,
                "[data-action='体温提报']" : this.actionTwtb
            };
        },
        initView : function() {
            this._initAdvanceQuery();
            this._initTable();
        },
        actionAdd : function() {
            var params = {
                pageNumber : 1,
            };
            var sldata = WIS_EMAP_SERV.getData(bs.api.pageModel, 'cxwdjbxxcjsl', params);
            if (sldata.rows[0].CNT == 0) {
                $.warn(appConfig.sfxsyw ? 'Please complete the basic information collection first！' : '请先完成基本信息采集上报！');
                return false;
            }
            var data = WIS_EMAP_SERV.getData(bs.api.pageModel, 'getTodayHasReported', params);
            var rows = data.rows;
            if (rows.length !== 0) {
                $.warn(appConfig.sfxsyw ? 'Filled today！' : '今日已填报！');
                return false;
            }
            var sfksbData = checkMrbpaSfksb();
            if (sfksbData.success) {
                if (!sfksbData.SFKSB_TQ) {
                    $.warn(sfksbData.TQTBTS || '根据东南大学疫情防控工作安排，每天执行晨/午检制度（申报早晨、中午体温）。为方便师生一次性填报，将健康申报时间调整为11点～15点。');
                    return false;
                }
                if (!sfksbData.SFKSB_CS) {
                    $.warn('每日健康申报截止时间' + sfksbData.JZSJ);
                    return false;
                }
            }
            var dailyReportNewTpl = utils.loadCompiledPage('dailyReportSave');
            bs.dataReady = false;
            $.bhPaperPileDialog.show({
                content : dailyReportNewTpl.render({
                    tips : appConfig.sfxsyw ? appConfig.tips_yw : appConfig.tips,
                    sfxsyw : appConfig.sfxsyw,
                    title : appConfig.sfxsyw ? "Add" : "新建"
                }),
                title : appConfig.sfxsyw ? "Add" : "新建",
                ready : function($header, $body, $footer) {
                    var userInfo = appConfig.userInfoFromDB;
                    dailyReportSave.initialize(appConfig.sfxsyw, userInfo.RYSFLB);
                    var tempFormData = {};
                    // USER_INFO为内置变量
                    tempFormData.USER_ID = USERID;
                    tempFormData.PHONE_NUMBER = userInfo.PHONE_NUMBER;
                    tempFormData.IDCARD_NO = userInfo.IDENTITY_CREDENTIALS_NO;
                    tempFormData.GENDER_CODE = userInfo.GENDER_CODE;
                    var data = WIS_EMAP_SERV.getData(bs.api.pageModel, appConfig.sfxsyw ? 'getMyTodayReportWidYwb'
                            : 'getMyTodayReportWid', {
                        pageNumber : 1,
                    });
                    if (data.rows && data.rows[0]) {
                        $.extend(tempFormData, data.rows[0]);
                    }
                    data = WIS_EMAP_SERV.getData(bs.api.pageModel, appConfig.sfxsyw ? 'getLatestDailyReportDataYwb'
                            : 'getLatestDailyReportData', {
                        pageNumber : 1,
                        pageSize : 10,
                    });
                    if (data.rows && data.rows[0]) {
                        bs.oldData = data.rows[0];
                        bs.dbrq = bs.oldData.NEED_CHECKIN_DATE; // 对比日期
                        delete bs.oldData['NEED_CHECKIN_DATE'];
                        $.extend(tempFormData, bs.oldData);
                    }
                    tempFormData.CLASS_CODE = userInfo.CLASS_CODE; // 班级
                    tempFormData.CLASS = userInfo.CLASS; // 班级
                    tempFormData.RYSFLB = userInfo.RYSFLB;
                    tempFormData.USER_NAME = userInfo.USER_NAME;
                    tempFormData.DEPT_CODE = userInfo.DEPT_CODE;
                    tempFormData.DEPT_NAME = userInfo.DEPT_NAME;
                    tempFormData.USER_NAME_EN = userInfo.USER_NAME_EN;
                    tempFormData.CREATED_AT = dateFormat("YYYY-mm-dd HH:MM", getChineseTime());
                    $("#emapForm").emapForm("setValue", tempFormData);
                    bs.dataReady = true;
                },
                close : function() {
                    $('#emapdatatable').emapdatatable('reload',
                            $('#emapdatatable').emapdatatable().data().currentQueryParams);
                }
            });
        },
        actionEdit : function(e) {
            var id = $(e.target).attr("data-x-wid");
            var dailyReportEditTpl = utils.loadCompiledPage('dailyReportSave');
            var action = 'getMyDailyReportDatas';
            if (appConfig.sfxsyw) {
                action = 'getMyDailyReportDatasEn';
            }
            var data = WIS_EMAP_SERV.getData(bs.api.pageModel, action, {
                pageNumber : 1,
                pageSize : 10,
                WID : id
            });
            bs.dataReady = false;
            $.bhPaperPileDialog.show({
                content : dailyReportEditTpl.render({}),
                title : "编辑",
                ready : function($header, $body, $footer) {
                    var zhbkg = appConfig.zhb_switch;
                    var sfglkg = appConfig.sfgl_switch;
                    if (!(zhbkg == '1' || zhbkg == '0' || zhbkg == '')
                            || !(sfglkg == '1' || sfglkg == '0' || sfglkg == '')) {
                        var data1 = getCheckinFormTips();
                        zhbkg = data1.zhb_switch;
                        sfglkg = data1.sfgl_switch;
                    }
                    dailyReportSave.initialize(appConfig.sfxsyw, data.rows[0].RYSFLB, zhbkg, sfglkg);
                    $("#emapForm").emapForm("setValue", data.rows[0]);
                    bs.dataReady = true;
                }
            });
        },
        actionDetail : function(e) {
            var self = this;
            var id = $(e.target).attr("data-x-wid");
            var dailyReportViewTpl = utils.loadCompiledPage('dailyReportSave');
            var data = WIS_EMAP_SERV.getData(bs.api.pageModel, appConfig.sfxsyw ? 'getMyDailyReportDatasEn'
                    : 'getMyDailyReportDatas', {
                pageNumber : 1,
                pageSize : 10,
                WID : id
            });
            $.bhPaperPileDialog.show({
                content : dailyReportViewTpl.render({}),
                title : "查看",
                ready : function($header, $body, $footer) {
                    dailyReportView.initialize(data.rows[0], appConfig.sfxsyw);
                    comBs.showHiddens_mrbpa(data.rows[0]);
                },
                render : function() {
                    $('#tips').remove();
                }
            });
        },
        actionDelete : function() {
            var row = $("#emapdatatable").emapdatatable("checkedRecords");
            if (row.length > 0) {
                var params = row.map(function(el) {
                    // return {XSBH:el.XSBH, XXX:el.XXX}; //模型主键
                });
                bs.del(params).done(function(data) {
                    alert("数据删除成功");
                    $('#emapdatatable').emapdatatable('reload');
                });
            }
        },
        actionCustomColumn : function() {
            $('#emapdatatable').emapdatatable('selectToShowColumns');
        },
        _initAdvanceQuery : function() {
            var action = 'getMyDailyReportDatas';
            if (appConfig.sfxsyw) {
                action = 'getMyDailyReportDatasEn';
            }
            var searchData = WIS_EMAP_SERV.getModel(bs.api.pageModel, action, "search");
            var $query = $('#emapAdvancedQuery').emapAdvancedQuery({
                data : searchData,
                contextPath : contextPath,
                schema : true
            });
            $query.on('search', this._searchCallback);
        },
        _searchCallback : function(e, data, opts) {
            $('#emapdatatable').emapdatatable('reload', {
                querySetting : data
            });
        },
        _initTable : function() {
            var action = 'getMyDailyReportDatas';
            var czname = '操作';
            var xqname = '详情';
            if (appConfig.sfxsyw) {
                action = 'getMyDailyReportDatasEn';
                czname = 'operation';
                xqname = 'details';
            }
            var tableOptions = {
                pagePath : bs.api.pageModel,
                action : action,
                params : {
                    rysflb : appConfig.userInfoFromDB.RYSFLB
                },
                minLineNum : 10,
                customColumns : [ {
                    colIndex : '0',
                    type : 'tpl',
                    column : {
                        text : czname,
                        align : 'center',
                        cellsAlign : 'center',
                        cellsRenderer : function(row, column, value, rowData) {
                            var html = '<a href="javascript:void(0)" data-action="detail" data-x-wid=' + rowData.WID
                                    + '>' + xqname + '</a>';
                            if (rowData.NEED_CHECKIN_DATE == bs.curDate) {
                                if ((bs.sfktb1 && !rowData.DZ_DTWJTW) || (bs.sfktb2 && !rowData.DZ_DTWSJCTW)) {
                                    html += ' | <a href="javascript:void(0)" data-action="体温提报" data-x-index=' + row
                                            + '>体温提报</a>';
                                }
                            }
                            return html;
                        }
                    }
                } ]
            };
            $('#emapdatatable').emapdatatable(tableOptions);
        },
        // 体温提报
        actionTwtb : function(event) {
            var self = this;
            var data = checkTwSetting();
            if (data.success) {
                if (data.SFKTB1 || data.SFKTB2) {
                    if (data.SFKTB1) {
                        if (!data.SFKTB1_TQ) {
                            $.warn('"午检温度"填报开始时间' + data.WUW_KSSJ);
                            return false;
                        }
                    } else if (data.SFKTB2) {
                        if (!data.SFKTB2_TQ) {
                            $.warn('"晚检温度"填报开始时间' + data.WANW_KSSJ);
                            return false;
                        }
                    }
                    var contentHtml = '<div id="twtbform"></div>';
                    BH_UTILS.bhWindow(contentHtml, '体温提报', null, {
                        width : 400,
                        height : 400
                    }, function(win) {
                        var $form = $('#twtbform');
                        if (!$form.emapValidate('validate')) {
                            return false;
                        }
                        var formData = $form.emapForm('getValue');
                            bs.saveTwtb(formData).done(
                                function(resp) {
                                    if (resp.success) {
                                        $.msg("体温提报成功");
                                        $('#emapdatatable').emapdatatable('reload',
                                                $('#emapdatatable').emapdatatable().data().currentQueryParams);
                                        win.jqxWindow('close');
                                    } else {
                                        $.err(resp.msg || "操作失败，请联系管理员");
                                        return false;
                                    }
                                });
                    });
                    var $form = $('#twtbform');
                    var mode = WIS_EMAP_SERV.getModel(bs.api.pageModel, appConfig.sfxsyw ? 'twtbyw' : 'twtbbd', 'form',
                            {
                                rysflb : appConfig.userInfoFromDB.RYSFLB
                            });
                    $form.emapForm({
                        root : WIS_EMAP_SERV.getContextPath(), // 附件上传时必备属性
                        data : mode,
                        model : 'v',
                        textareaEasyCheck : true
                    });
                    var rowData = WIS_EMAP_SERV.getData(bs.api.pageModel, appConfig.sfxsyw ? 'twtbyw' : 'twtbbd', {
                        pageNumber : 1,
                        pageSize : 10
                    });
                    $form.emapForm('setValue', rowData.rows[0]);
                }
            }
        }
    };
    return viewConfig;
});
