package com.example.freelanceplatformspringapp.Complaints.Controllers;
import com.example.freelanceplatformspringapp.Complaints.Entity.Response;
import com.example.freelanceplatformspringapp.Complaints.Services.IResponseInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/{id-claim}")
public class ResponseController {
    IResponseInterface ir;
    @GetMapping("/retrieve-All-resposes")
    public List<Response> retrieveAllResposes(){
        List<Response> responses = ir.getResponseList();
        return responses;
    }
    @GetMapping("/retrieve-response/{id-response}")
    public Response retrieveResponse(@PathVariable("id-response") Long idResponse){
        Response response = ir.getResponseById(idResponse).orElse(null);
        return response;
    }
    @PostMapping("/reply")
    public Response replyResponse(@RequestBody Response response){
        ir.addResponse(response);
        return response;
    }
    @PutMapping("/update-response")
    public Response updateResponse(@RequestBody Response r){
        Response response = ir.updateResponse(r);
        return response;
    }
    @DeleteMapping("/delete-response/{id-response}")
    public void deleteResponse(@PathVariable("id-response") Long r){
        ir.deleteResponse(r);
    }
}