<template>
  <div class="person-card" @click="createChat">
    <div class="info">
      <el-icon :size="16" style="margin: auto 5px auto 10px;"><CirclePlus /></el-icon>
      <div class="info-detail">
        <div class="name" > New Chat </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

</script>

<style lang="scss" scoped>
.person-card {
  width: 200px;
  height: 60px;
  border-radius: 10px;
  background-color: var(--md-ref-palette-neutral100);
  border: 1px solid var(--md-ref-palette-neutral-variant60);
  position: relative;
  cursor: pointer;
  .info {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 90%;
    transform: translate(-50%, -50%);
    display: flex;
    .info-detail {
      margin-top: 5px;
      margin-left: 20px;
      .name {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 5px;
      }
    }
  }
  &:hover {
    background-color: var(--md-ref-palette-neutral-variant95);
    transition: 0.3s;
    box-shadow: 3px 2px 10px 0px rgb(181, 187, 192);
    border: 1px solid var(--md-sys-color-primary-light);
    .info {
      color: var(--md-sys-color-primary-light);
    }
  }
}
.activeCard {
  background-color: var(--md-ref-palette-neutral-variant95);
  transition: 0.3s;
  box-shadow: 3px 2px 10px 0px rgb(181, 187, 192);
  border: 1px solid var(--md-sys-color-primary-light);
  .info {
    color: var(--md-sys-color-primary-light);
  }
}
</style>
