<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div>
        预览部分
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const breadCrumbList = reactive([
    {
        name: '应用管理',
        code: 'app-management'
    },
    {
        name: '详情',
        code: 'app-detail'
    }
])

function initData() {
    
}

onMounted(() => {
    initData()
})
</script>

<style lang="scss">
.zqy-seach-table {

}
</style>