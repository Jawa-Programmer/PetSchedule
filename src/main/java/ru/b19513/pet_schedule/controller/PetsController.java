package ru.b19513.pet_schedule.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.b19513.pet_schedule.controller.entity.FeedNoteDTO;
import ru.b19513.pet_schedule.controller.entity.PetDTO;
import ru.b19513.pet_schedule.controller.entity.StatusDTO;
import ru.b19513.pet_schedule.controller.entity.enums.Gender;
import ru.b19513.pet_schedule.controller.entity.enums.PetType;
import ru.b19513.pet_schedule.service.PetService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/pets")
public class PetsController {

    private final PetService petService;
    @Autowired
    public PetsController(PetService petService) {
        this.petService = petService;
    }

    @ApiOperation(value = "Добавить нового питомца")
    @PostMapping("/{groupId}{name}{description}{gender}{petType}")
    public ResponseEntity<PetDTO> createPet(@PathVariable long groupId, @PathVariable String name, @PathVariable String description,
                                            @PathVariable Gender gender, @PathVariable PetType petType) {
        PetDTO petDTO = petService.createPet(groupId, name, description, gender, petType);
        return new ResponseEntity<>(petDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Обновить сведения о питомце")
    @PutMapping("/")
    public ResponseEntity<PetDTO> updatePet(@RequestParam PetDTO petDTOInput) {
        PetDTO petDTO = petService.updatePet(petDTOInput);
        return new ResponseEntity<>(petDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить список питомцев по id группы")
    @GetMapping("/{groupId}")
    public ResponseEntity<Collection<PetDTO>> getPets(@PathVariable long groupId) {
        Collection<PetDTO> PetDTOCollection = petService.getPets(groupId);
        return new ResponseEntity<>(PetDTOCollection, HttpStatus.OK);
    }

    @ApiOperation(value = "Удалить питомца и все связанные с ним напоминания и записи")
    @DeleteMapping("/{petId}")
    public ResponseEntity<StatusDTO> deletePet(@PathVariable long petId) {
        StatusDTO statusDTO = petService.deletePet(petId);
        return new ResponseEntity<>(statusDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Создать запись о кормлении")
    @PostMapping("/{petId}{userId}{comment}")
    public ResponseEntity<FeedNoteDTO> createFeedNote (@PathVariable long petId, @PathVariable long userId, @PathVariable String comment){
        FeedNoteDTO feedNoteDTO = petService.createFeedNote(petId, userId, comment);
        return new ResponseEntity<>(feedNoteDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить список записей о кормлении")
    @GetMapping("/{petId}")
    public ResponseEntity<Collection<FeedNoteDTO>> getFeedNotes (@PathVariable long petId){
        Collection<FeedNoteDTO> feedNoteDTOCollection = petService.getFeedNotes(petId);
        return new ResponseEntity<>(feedNoteDTOCollection, HttpStatus.OK);
    }

    @ApiOperation(value = "Найти записи о кормежках по времени и дате")
    @GetMapping("/{petId}{from}{to}")
    public ResponseEntity<Collection<FeedNoteDTO>> findFeedNotesByDate (@PathVariable long petId, @PathVariable LocalDateTime from, @PathVariable LocalDateTime to){
        Collection<FeedNoteDTO> feedNoteDTOCollection = petService.findFeedNotesByDate(petId, from, to);
        return new ResponseEntity<>(feedNoteDTOCollection, HttpStatus.OK);
    }
}
