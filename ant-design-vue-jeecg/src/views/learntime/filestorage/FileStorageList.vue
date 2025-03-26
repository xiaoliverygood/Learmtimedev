<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if='username == "admin"'>
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('file_storage')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;" v-if='username == "admin"'>
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text, record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadErrorFile(text,record)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record" v-if='username == "admin"'>
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <file-storage-modal ref="modalForm" @ok="modalFormOk"></file-storage-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import FileStorageModal from './modules/FileStorageModal'
  import { putAction } from '@api/manage'
  import store from '@/store/'
  import Vue from 'vue'
  import { USER_INFO } from '@/store/mutation-types'

  export default {
    name: 'FileStorageList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      FileStorageModal
    },
    data () {
      return {
        description: 'file_storage管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'文件存储的位置',
            align:"center",
            dataIndex: 'url',
            scopedSlots: { customRender: 'fileSlot' }
          },
          {
            title:'说明',
            align:"center",
            dataIndex: 'conent'
          },
          {
            title:'下载次数',
            align:"center",
            dataIndex: 'downloadCount'
          },
          {
            title:'创建人',
            align:"center",
            dataIndex: 'createBy'
          },
          {
            title:'创建日期',
            align:"center",
            dataIndex: 'createTime'
          },
          {
            title:'最后下载',
            align:"center",
            dataIndex: 'updateBy'
          },
          {
            title:'最后下载日期',
            align:"center",
            dataIndex: 'updateTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/learntime/fileStorage/list",
          delete: "/learntime/fileStorage/delete",
          edit: "/learntime/fileStorage/edit",
          deleteBatch: "/learntime/fileStorage/deleteBatch",
          exportXlsUrl: "/learntime/fileStorage/exportXls",
          importExcelUrl: "learntime/fileStorage/importExcel",

        },
        dictOptions:{},
        superFieldList:[],
        username: ''
      }
    },
    created() {
    this.getSuperFieldList();
    this.getUsername();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'url',text:'文件存储的位置'})
        fieldList.push({type:'string',value:'conent',text:'说明'})
        fieldList.push({type:'int',value:'downloadCount',text:'下载次数'})
        this.superFieldList = fieldList
      },
      getUsername(){
        this.username = Vue.ls.get(USER_INFO).username
      },
      downloadErrorFile(text,record){
        JeecgListMixin.methods.downloadFile(text)
        record.downloadCount += 1
        record.updateBy = store.getters.nickname
        putAction(this.url.edit,{id:record.id,downloadCount:record.downloadCount,updateBy:record.updateBy}).then(res => {
          if (res.success) {
            console.log(res.result)
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>