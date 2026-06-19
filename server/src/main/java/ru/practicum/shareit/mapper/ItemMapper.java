package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;

@Component
public class ItemMapper {

    public Item mapToItem(NewItemRequestDto newItemRequestDto) {
        Item item = new Item();
        item.setName(newItemRequestDto.getName());
        item.setDescription(newItemRequestDto.getDescription());
        item.setAvailable(newItemRequestDto.getAvailable());

        return item;
    }

    public ItemResponseDto mapToItemDto(Item item) {
        ItemResponseDto itemDto = new ItemResponseDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwnerId(item.getUser().getId());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public Comment mapToComment(CommentRequestDto comment) {
        Comment newComment = new Comment();
        newComment.setText(comment.getText());
        return newComment;
    }

    public CommentResponseDto mapToCommentDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setAuthorName(comment.getUser().getName());
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());

        return dto;
    }

}